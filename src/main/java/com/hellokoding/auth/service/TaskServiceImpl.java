package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.TaskState;
import com.hellokoding.auth.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Override
    public Task findById(BigInteger id) {
        Optional<Task> opt = taskRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteById(BigInteger id) {
        Task task = taskRepository.getOne(id);
        task.getOwner().retMoneyForTask(task.getPrice());
        taskRepository.deleteById(id);
    }

    @Override
    public Page<Task> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Task> list;

        List<Task> tasks = taskRepository.findTasksByStateOrderByIdDesc(TaskState.OPEN);

        if (tasks.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, tasks.size());
            list = tasks.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), tasks.size());
    }


    @Override
    public void markExpired() {
        List<Task> tasks = taskRepository.findAll();
        markExpired(tasks.parallelStream().collect(Collectors.toSet()));
    }

    @Override
    public void markExpired(Set<Task> tasks) {
        if (!tasks.isEmpty()) {
            for (Task task : tasks) {
                if (task.getState()!= TaskState.EXPIRED || task.getState()!= TaskState.IN_DISPUTE) {
                    task.checkExpired();
                    taskRepository.save(task);
                }
            }
        }
    }
}

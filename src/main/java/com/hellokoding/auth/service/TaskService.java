package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public interface TaskService {
    Task findById(BigInteger id);

    void save(Task task);

    List<Task> findAll();

    void markExpired();

    void deleteById(BigInteger id);

    Page<Task> findPaginated(Pageable pageable);

    void markExpired(Set<Task> tasks);
}

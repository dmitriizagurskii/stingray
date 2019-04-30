package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.TaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, BigInteger> {
    List<Task> findTasksByStateOrderByIdDesc(TaskState state);
}

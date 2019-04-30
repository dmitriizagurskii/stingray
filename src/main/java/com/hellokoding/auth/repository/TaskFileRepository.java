package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.TaskFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskFileRepository extends JpaRepository<TaskFile, Long> {
    TaskFile findByFileName(String FileName);
}

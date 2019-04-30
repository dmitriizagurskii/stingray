package com.hellokoding.auth.service;

import com.hellokoding.auth.model.TaskFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface TaskFileService {
    TaskFile getTaskFile(MultipartFile multipartFile);

    TaskFile findByFileName(String username);

    TaskFile findById(Long id);

    List<TaskFile> findAll();

    void deleteAll(Set<TaskFile> files);

    TaskFile save(MultipartFile multipartFile);

    void deleteById(Long id);
}

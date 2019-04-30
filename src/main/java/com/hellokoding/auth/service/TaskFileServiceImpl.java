package com.hellokoding.auth.service;

import com.hellokoding.auth.exceptions.FileStorageException;
import com.hellokoding.auth.model.TaskFile;
import com.hellokoding.auth.repository.TaskFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class TaskFileServiceImpl implements TaskFileService {

    @Autowired
    private TaskFileRepository taskFileRepository;

    @Override
    public TaskFile save(MultipartFile multipartFile) {
        return taskFileRepository.save(this.getTaskFile(multipartFile));
    }

    @Override
    public void deleteById(Long id) {
        taskFileRepository.deleteById(id);
    }

    @Override
    public TaskFile getTaskFile(MultipartFile multipartFile) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        try {
            //Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            return new TaskFile(fileName, multipartFile.getContentType(), multipartFile.getBytes());
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public TaskFile findByFileName(String fileName) {
        return taskFileRepository.findByFileName(fileName);
    }

    @Override
    public TaskFile findById(Long id) {
        return taskFileRepository.findById(id).orElse(null);
    }

    @Override
    public List<TaskFile> findAll() {
        return taskFileRepository.findAll();
    }

    @Override
    public void deleteAll(Set<TaskFile> files) {
        taskFileRepository.deleteAll(files);
    }
}

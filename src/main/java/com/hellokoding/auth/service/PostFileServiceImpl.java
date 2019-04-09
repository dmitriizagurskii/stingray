package com.hellokoding.auth.service;

import com.hellokoding.auth.exceptions.FileStorageException;
import com.hellokoding.auth.model.PostFile;
import com.hellokoding.auth.repository.PostFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class PostFileServiceImpl implements PostFileService {

    @Autowired
    private PostFileRepository postFileRepository;

    @Override
    public PostFile save(MultipartFile multipartFile) {
        return postFileRepository.save(this.getPostFile(multipartFile));
    }

    @Override
    public void deleteById(Long id) {
        postFileRepository.deleteById(id);
    }

    @Override
    public PostFile getPostFile(MultipartFile multipartFile) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        try {
            //Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            return new PostFile(fileName, multipartFile.getContentType(), multipartFile.getBytes());
        } catch (IOException ex) {
            return null;
        }
    }

    @Override
    public PostFile findByFileName(String fileName) {
        return postFileRepository.findByFileName(fileName);
    }

    @Override
    public PostFile findById(Long id) {
        return postFileRepository.findById(id).orElse(null);
    }

    @Override
    public List<PostFile> findAll() {
        return postFileRepository.findAll();
    }

    @Override
    public void deleteAll(Set<PostFile> files) {
        postFileRepository.deleteAll(files);
    }
}

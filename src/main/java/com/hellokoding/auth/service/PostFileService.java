package com.hellokoding.auth.service;

import com.hellokoding.auth.model.PostFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public interface PostFileService {
    PostFile getPostFile(MultipartFile postFile);

    PostFile findByFileName(String username);

    PostFile findById(Long id);

    List<PostFile> findAll();

    void deleteAll(Set<PostFile> files);

    PostFile save(PostFile postFile);
}

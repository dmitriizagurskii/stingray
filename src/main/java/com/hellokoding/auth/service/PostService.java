package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostService {
    Post findById(Long id);

    void save(Post post);

    List<Post> findAll();

    void deleteById(Long id);

    Page<Post> findPaginated(Pageable pageable);

}

package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;

import java.util.List;

public interface PostService {
    public Post findById(Long id);
    public void save (Post post);
    public List<Post> findAll();
    public void deleteById(Long id);
}

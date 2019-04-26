package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.PostState;
import com.hellokoding.auth.repository.PostRepository;
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
public class PostServiceImpl implements PostService{

    @Autowired
    PostRepository postRepository;

    @Override
    public Post findById(BigInteger id) {
        Optional<Post> opt = postRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public void deleteById(BigInteger id) {
        Post post = postRepository.getOne(id);
        post.getOwner().retMoneyForPost(post.getPrice());
        postRepository.deleteById(id);
    }

    @Override
    public Page<Post> findPaginated(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        List<Post> list;

        List<Post> posts = postRepository.findPostsByStateOrderByIdDesc(PostState.OPEN);

        if (posts.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, posts.size());
            list = posts.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), posts.size());
    }


    @Override
    public void markExpired() {
        List<Post> posts = postRepository.findAll();
        markExpired(posts.parallelStream().collect(Collectors.toSet()));
    }

    @Override
    public void markExpired(Set<Post> posts) {
        if (!posts.isEmpty()) {
            for (Post post: posts) {
                if (post.getState()!=PostState.EXPIRED || post.getState()!=PostState.IN_DISPUTE) {
                    post.checkExpired();
                    postRepository.save(post);
                }
            }
        }
    }
}

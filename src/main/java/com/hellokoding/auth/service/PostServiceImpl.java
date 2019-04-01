package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{

    @Autowired
    PostRepository postRepository;

    @Override
    public Post findById(Long id) {
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
    public void deleteById(Long id) {
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

        List<Post> posts = postRepository.findPostsByConfirmedIsFalse();

        if (posts.size() < startItem) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(startItem + pageSize, posts.size());
            list = posts.subList(startItem, toIndex);
        }

        return new PageImpl<>(list, PageRequest.of(currentPage, pageSize), posts.size());
    }


    @Override
    public void deleteExpired() {
        List<Post> postList = postRepository.findAll();
        if (!postList.isEmpty()) {
            for (Post post: postList) {
                if (post.isExpired()) {
                    deleteById(post.getId());
                }
            }
        }
    }
}

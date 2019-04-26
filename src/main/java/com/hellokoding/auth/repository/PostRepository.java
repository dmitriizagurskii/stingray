package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.PostState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface PostRepository extends JpaRepository<Post, BigInteger> {
    List<Post> findPostsByStateOrderByIdDesc(PostState state);
}

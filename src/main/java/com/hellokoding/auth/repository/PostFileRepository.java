package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {
    PostFile findByFileName(String FileName);
}

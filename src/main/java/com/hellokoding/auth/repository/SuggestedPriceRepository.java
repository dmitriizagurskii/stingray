package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedPriceRepository extends JpaRepository<SuggestedPrice, Long> {
    SuggestedPrice findByCandidatePostAndSuggester(Post post, User user);
}

package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedPriceRepository extends JpaRepository<SuggestedPrice, Long> {
    SuggestedPrice findByCandidateTaskAndSuggester(Task task, User user);
}

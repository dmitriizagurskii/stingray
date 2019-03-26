package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.SuggestedPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuggestedPriceRepository extends JpaRepository<SuggestedPrice, Long> {
}

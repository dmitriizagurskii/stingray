package com.hellokoding.auth.repository;

import com.hellokoding.auth.model.SuggestedPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuggestedPriceRepository extends JpaRepository<SuggestedPrice, Long> {
}

package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Rating;

public interface RatingService {
    Rating findById(Long id);

    void deleteById(Long id);

    void save(Rating rating);
}

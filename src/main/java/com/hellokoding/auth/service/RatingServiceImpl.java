package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Rating;
import com.hellokoding.auth.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    RatingRepository ratingRepository;

    @Override
    public Rating findById(Long id) {
        Optional<Rating> opt = ratingRepository.findById(id);
        return opt.orElse(null);
    }

    @Override
    public void save(Rating rating) {
        ratingRepository.save(rating);
    }

    @Override
    public void deleteById(Long id) {
        ratingRepository.deleteById(id);
    }
}

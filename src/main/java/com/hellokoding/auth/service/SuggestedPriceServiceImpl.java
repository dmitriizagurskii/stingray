package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.SuggestedPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class SuggestedPriceServiceImpl implements SuggestedPriceService {

    @Autowired
    private SuggestedPriceRepository suggestedPriceRepository;

    @Override
    public void delete(SuggestedPrice price) {
        suggestedPriceRepository.delete(price);
    }

    @Override
    public SuggestedPrice getSuggestedPrice(User user, Task task) {
        SuggestedPrice suggestedPrice = suggestedPriceRepository.findByCandidateTaskAndSuggester(task, user);
        if (suggestedPrice == null) {
            return new SuggestedPrice(task, user);
        }
        return suggestedPrice;
    }

    @Override
    public void deleteAll(Set<SuggestedPrice> price) {
        suggestedPriceRepository.deleteAll(price);
    }

    @Override
    public void save(SuggestedPrice price) {
        suggestedPriceRepository.save(price);
    }
}

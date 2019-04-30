package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;

import java.util.Set;

public interface SuggestedPriceService {
    public SuggestedPrice getSuggestedPrice(User user, Task task);
    public void save(SuggestedPrice price);
    public void delete(SuggestedPrice price);
    public void deleteAll(Set<SuggestedPrice> price);
}

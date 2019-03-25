package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.SuggestedPriceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class SuggestedPriceServiceImpl implements SuggestedPriceService {

    @Autowired
    private SuggestedPriceRepository suggestedPriceRepository;

    @Override
    public void delete(SuggestedPrice price) {
        suggestedPriceRepository.delete(price);
    }

    //@Query("")
    //todo write a proper query

    @Override
    public SuggestedPrice getSuggestedPrice(User user, Post post) {
        List<SuggestedPrice> allSuggestedPrices = suggestedPriceRepository.findAll();
        for (Iterator<SuggestedPrice> suggestedPriceIterator = allSuggestedPrices.iterator(); suggestedPriceIterator.hasNext(); ) {

            SuggestedPrice suggestedPrice = suggestedPriceIterator.next();
            if (suggestedPrice.getCandidatePost() == post && suggestedPrice.getSuggester() == user)
                return suggestedPrice;
        }

        return new SuggestedPrice(post, user);
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

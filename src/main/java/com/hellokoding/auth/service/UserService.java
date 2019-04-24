package com.hellokoding.auth.service;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.model.User;

import java.util.Set;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    User findById(Long id);

    User getCurrentUser();

    void topUpBalance(User user, Integer sum);

    void withdraw(User user, Integer sum);

    void moneyTransferFromTo(User owner, User manager, Integer price);
}

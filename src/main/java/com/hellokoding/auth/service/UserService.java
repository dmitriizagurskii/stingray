package com.hellokoding.auth.service;

import com.hellokoding.auth.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    User findById(Long id);

    User findCurrentUser();

    void topUpBalance(User user, Integer sum);

    void withdraw(User user, Integer sum);

}

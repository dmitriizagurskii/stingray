package com.hellokoding.auth.service;

import com.hellokoding.auth.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);

    User findById(Long id);

    void topUpBalance(User user, Integer sum);
}

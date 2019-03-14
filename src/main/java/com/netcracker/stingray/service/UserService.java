package com.netcracker.stingray.service;

import com.netcracker.stingray.model.User;

public interface UserService {
    void save(User user);

    User findByUsername(String username);
}

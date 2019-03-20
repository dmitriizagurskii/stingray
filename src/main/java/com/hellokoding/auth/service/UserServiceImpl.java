package com.hellokoding.auth.service;

import com.hellokoding.auth.model.User;
import com.hellokoding.auth.repository.RoleRepository;
import com.hellokoding.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void save(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(new HashSet<>(roleRepository.findAll()));
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findById(Long id) {
        if (userRepository.findById(id).isPresent()) {
            return userRepository.findById(id).get();
        } else
            return null;
    }

    @Override
    public void topUpBalance(User user, Integer sum) {
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);
    }


}

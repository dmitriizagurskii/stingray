package com.netcracker.stingray.service;

public interface SecurityService {
    String findLoggedInUsername();

    void autoLogin(String username, String password);
}

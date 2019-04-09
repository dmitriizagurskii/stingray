package com.hellokoding.auth.service;

import com.hellokoding.auth.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);

    void deleteById(Long id);

    ChatMessage findById(Long id);

    List<ChatMessage> findAll();
}

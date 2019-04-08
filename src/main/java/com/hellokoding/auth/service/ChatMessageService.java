package com.hellokoding.auth.service;

import com.hellokoding.auth.model.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);

    void deleteById(Long id);

    ChatMessage findById(Long id);
}

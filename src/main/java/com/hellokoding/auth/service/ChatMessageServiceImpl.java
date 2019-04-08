package com.hellokoding.auth.service;

import com.hellokoding.auth.model.ChatMessage;
import com.hellokoding.auth.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public void deleteById(Long id) {
        chatMessageRepository.deleteById(id);
    }

    @Override
    public ChatMessage findById(Long id) {
        return chatMessageRepository.findById(id).orElse(null);
    }
}

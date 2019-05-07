package com.hellokoding.auth.web;

import com.hellokoding.auth.model.ChatMessage;
import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.service.ChatMessageService;
import com.hellokoding.auth.service.TaskService;
import com.hellokoding.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigInteger;

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage.task.{taskId}")
    @SendTo("/public/task/{taskId}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable BigInteger taskId) {
        chatMessage.setTask(taskService.findById(taskId));
        return chatMessageService.save(chatMessage);
    }

    @MessageMapping("/chat.addUser.task.{taskId}")
    @SendTo("/public/task/{taskId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor, @DestinationVariable BigInteger taskId) {
        // Add username in web socket session
//        if (!verifyUser(chatMessage.getSenderUsername(), taskId)) {
//            throw new SecurityException();
//        }
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSenderUsername());
        return chatMessage;
    }

    @GetMapping("/chat")
    public String showChat(Model model){
        model.addAttribute("user", userService.getCurrentUser());
        return "chat";
    }

    private boolean verifyUser(String username, BigInteger taskId){
        Task task = taskService.findById(taskId);
        return (username == task.getOwner().getUsername() || username == task.getExecutor().getUsername());
    }
}
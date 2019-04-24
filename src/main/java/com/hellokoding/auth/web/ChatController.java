package com.hellokoding.auth.web;

import com.hellokoding.auth.model.ChatMessage;
import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.service.ChatMessageService;
import com.hellokoding.auth.service.PostService;
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

@Controller
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private PostService postService;

    @Autowired
    private ChatMessageService chatMessageService;

    @MessageMapping("/chat.sendMessage.post.{postId}")
    @SendTo("/public/post/{postId}")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable Long postId) {
        chatMessage.setPost(postService.findById(postId));
        return chatMessageService.save(chatMessage);
    }

    @MessageMapping("/chat.addUser.post.{postId}")
    @SendTo("/public/post/{postId}")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor, @DestinationVariable Long postId) {
        // Add username in web socket session
//        if (!verifyUser(chatMessage.getSenderUsername(), postId)) {
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

    private boolean verifyUser(String username, Long postId){
        Post post = postService.findById(postId);
        return (username == post.getOwner().getUsername() || username == post.getManager().getUsername());
    }
}
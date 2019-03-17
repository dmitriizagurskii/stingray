package com.hellokoding.auth.web;

import com.hellokoding.auth.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/")
    public String showMainPage(Model model){
        model.addAttribute("posts", postRepository.findAll());
        return "posts";
    }

    @GetMapping("/unsupported")
    public String unsupported(Model model) {
        return "unsupported";
    }
}

package com.hellokoding.auth.web;

import com.hellokoding.auth.model.PostFile;
import com.hellokoding.auth.repository.PostFileRepository;
import com.hellokoding.auth.repository.PostRepository;
import com.hellokoding.auth.service.PostFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MainController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostFileService postFileService;

    @GetMapping("/")
    public String showMainPage(){
        return "redirect:/posts";
    }

    @GetMapping("/unsupported")
    public String unsupported() {
        return "unsupported";
    }
}

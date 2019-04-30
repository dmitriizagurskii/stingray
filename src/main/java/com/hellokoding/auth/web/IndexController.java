package com.hellokoding.auth.web;

import com.hellokoding.auth.repository.TaskRepository;
import com.hellokoding.auth.service.TaskFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController implements ErrorController {

    private static final String ERROR = "/error";

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskFileService taskFileService;

    @GetMapping("/")
    public String showMainPage(){
        return "redirect:/tasks";
    }

    @GetMapping("/unsupported")
    public String unsupported() {
        return "unsupported";
    }

    @GetMapping(ERROR)
    public String error() {
        return "no-page-err";
    }

    @Override
    public String getErrorPath() {
        return ERROR;
    }
}

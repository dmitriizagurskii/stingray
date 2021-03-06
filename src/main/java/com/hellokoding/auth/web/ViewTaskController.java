package com.hellokoding.auth.web;

import com.hellokoding.auth.model.*;
import com.hellokoding.auth.service.TaskFileService;
import com.hellokoding.auth.service.TaskService;
import com.hellokoding.auth.service.SuggestedPriceService;
import com.hellokoding.auth.service.UserService;
import com.hellokoding.auth.validator.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashSet;

@Controller
public class ViewTaskController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SuggestedPriceService suggestedPriceService;

    @Autowired
    private TaskFileService taskFileService;

    @Autowired
    private TaskValidator taskValidator;

    @GetMapping("/viewtask/{id}")
    public String showTask(@PathVariable("id") BigInteger id, Model model) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        taskService.markExpired(new HashSet<>(Arrays.asList(task)));

        User user = userService.getCurrentUser();

        boolean isOwner = (task.getOwner() == user); //|| user.getRoles().contains(Roles.ADMIN);

        model.addAttribute("user", user);
        model.addAttribute("task", task);

        switch (task.getState()) {
            case OPEN:
                if (isOwner) {
                    return "view-own-task";
                }

                model.addAttribute("suggestedPrice", suggestedPriceService.getSuggestedPrice(user, task));
                return "view-task";
            case FINISHED:
                if (isOwner || task.getExecutor() == user) {
                    model.addAttribute("ratingForm", new Rating());
                    return "view-finished-task";
                }
                else return "permission-denied";
            case EXPIRED:
                if (isOwner)
                    return "view-expired-task";
                else return "permission-denied";
            case IN_DISPUTE:
                if (isOwner || task.getExecutor() == user) {
                    model.addAttribute("ratingForm", new Rating());
                    return "view-dispute-task";
                }
                else return "permission-denied";
        }
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("ratingForm", new Rating());
        return "view-assigned-task";

    }


    @PostMapping(value = "/viewtask/{id}", params = "accepttask")
    public String acceptTask(@PathVariable("id") BigInteger id, @RequestParam String accepttask) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User user = userService.findByUsername(accepttask);
        if (user == null) {
            return "no-user-err";
        }

        user.addTaskToCandidates(task);

        taskService.save(task);

        return "redirect:/viewtask/{id}";
    }

    @PostMapping(value = "/viewtask/{id}", params = "rejecttask")
    public String rejectTask(@PathVariable("id") BigInteger id, @RequestParam String rejecttask) {

        Task task = taskService.findById(id);

        if (task == null) {
            return "no-task-err";
        }

        User user = userService.findByUsername(rejecttask);
        if (user == null) {
            return "no-user-err";
        }

        if (!user.getAcceptedTasks().contains(task)) {
            return "error";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, task);
        suggestedPrice.setValue(task.getPrice());
        user.removeTaskFromCandidates(task);

        suggestedPriceService.save(suggestedPrice);
        taskService.save(task);

        return "redirect:/viewtask/{id}";
    }

    @PostMapping(value = "/viewtask/{id}", params = "suggestprice")
    public String suggestPrice(@PathVariable("id") BigInteger id, @RequestParam String
            suggestprice, @ModelAttribute SuggestedPrice price, BindingResult result, Model model) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User user = userService.findByUsername(suggestprice);
        if (user == null) {
            return "no-user-err";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, task);
        suggestedPrice.setValue(price.getValue());
        suggestedPriceService.save(suggestedPrice);

        return "redirect:/viewtask/{id}";
    }

    @PostMapping(value = "/viewtask/{id}", params = "addfiles")
    public String uploadFile(@PathVariable("id") BigInteger id, @RequestParam("files") MultipartFile[] files) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        for (MultipartFile mf : files) {
            task.addTaskFile(taskFileService.save(mf));
        }

        taskService.save(task);
        return "redirect:/viewtask/{id}";
    }

    @PostMapping("/finishtask/{id}")
    public String finishTask(@PathVariable("id") BigInteger id, @ModelAttribute("ratingForm") Rating ratingForm) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User owner = task.getOwner();

        if (userService.getCurrentUser() == owner) {
            task.finish();
            ratingForm.setTask(task);
            ratingForm.setAuthor(owner.getUsername());
            ratingForm.getTask().rate(ratingForm);
        } else {
            task.setState(TaskState.READY);
            //todo:notify owner
        }

        taskService.save(task);
        return "redirect:/viewtask/{id}";
    }

    @PostMapping("/rateowner/{id}")
    public String rateOwner(@PathVariable("id") BigInteger id, @ModelAttribute("ratingForm") Rating ratingForm) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User executor = task.getExecutor();

        if (userService.getCurrentUser() == executor) {
            executor.setBalance(executor.getBalance()+task.getPrice());
            ratingForm.setTask(task);
            ratingForm.setAuthor(executor.getUsername());
            ratingForm.getTask().rate(ratingForm);
        } else {
            return "permission-denied";
        }

        taskService.save(task);
        return "redirect:/viewtask/{id}";
    }

    @PostMapping("/opendispute/{id}")
    public String openDispute(@PathVariable("id") BigInteger id) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        task.setState(TaskState.IN_DISPUTE);
        taskService.save(task);
        return "redirect:/viewtask/{id}";
    }


    @PostMapping(value = "/viewtask/{id}", params = "extendDeadline")
    public String extendDeadline(@PathVariable("id") BigInteger id, Task task, BindingResult result) {
        Task originalTask = taskService.findById(id);
        if (originalTask == null) {
            return "no-task-err";
        }

        originalTask.setDate(task.getDate());
        taskValidator.validate(originalTask, result);
        if (!result.hasErrors())
            taskService.save(originalTask);
        return "redirect:/viewtask/{id}";
    }
}

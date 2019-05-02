package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Task;
import com.hellokoding.auth.model.TaskState;
import com.hellokoding.auth.model.SuggestedPrice;
import com.hellokoding.auth.model.User;
import com.hellokoding.auth.service.*;
import com.hellokoding.auth.validator.TaskValidator;
import com.hellokoding.auth.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class TaskController {

    @Autowired
    private UserService userService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SuggestedPriceService suggestedPriceService;

    @Autowired
    private TaskFileService taskFileService;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private TaskValidator taskValidator;


    @GetMapping("/createtask")
    public String showTaskCreateForm(Task task, Model model) {

        User user = userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("task", task);
        return "create-task";
    }

    @PostMapping("/createtask")
    public String createTask(Model model, Task task, BindingResult result,
                             @RequestParam("files") MultipartFile[] files) {

        User user = userService.getCurrentUser();

        taskValidator.validate(task, result);
        userValidator.validateBalance(user, task.getPrice(), result);

        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "create-task";
        }


        if (!Arrays.stream(files).findFirst().get().isEmpty())
            for (MultipartFile mf : files) {
                task.addTaskFile(taskFileService.save(mf));
                taskService.save(task);
            }
        try {
            task.setDeadline();
        } catch (ParseException ex) {
            return "err"; //errPage??
        }
        user.createTask(task);
        taskService.save(task);
        return "redirect:/tasks";
    }


    @PostMapping("/deletetask/{id}")
    public String deleteTask(@PathVariable("id") BigInteger id) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        taskService.deleteById(id);

        return "redirect:/";
    }

    @GetMapping("/tasks")
    public String showMainPage(Model model, @RequestParam("page") Optional<Integer> page, @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        taskService.markExpired();

        Page<Task> taskPage = taskService.findPaginated(PageRequest.of(currentPage - 1, pageSize));
        model.addAttribute("taskPage", taskPage);

        int totalPages = taskPage.getTotalPages();
        if (totalPages > 1) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "tasks";
    }


    @GetMapping("/changetask/{id}")
    public String showTaskChangeForm(@PathVariable("id") BigInteger id, Model model) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User user = userService.getCurrentUser();

        if (task.getOwner() != user || (!task.getState().equals(TaskState.OPEN) && !task.getState().equals(TaskState.EXPIRED)) || !task.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        model.addAttribute("user", user);
        model.addAttribute("task", task);
        return "change-task";
    }

    @PostMapping("/changetask/{id}")
    public String changeTask(@PathVariable("id") BigInteger id, Task task, BindingResult result, Model model) {

        Task originalTask = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User owner = originalTask.getOwner();

        if (originalTask.getOwner() != owner || (!originalTask.getState().equals(TaskState.OPEN) && !originalTask.getState().equals(TaskState.EXPIRED)) || !originalTask.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        taskValidator.validate(task, result);
        userValidator.validateBalance(owner, task.getPrice() - originalTask.getPrice(), result);
        if (result.hasErrors()) {
            model.addAttribute("user", owner);
            return "change-task";
        }

        owner.changeTask(originalTask, task);
        taskService.save(originalTask);

        return "redirect:/viewtask/{id}?success";
    }

    @GetMapping("/candidates/{id}")
    public String showCandidates(@PathVariable("id") BigInteger id, Model model) {

        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        if (task.getOwner() != userService.getCurrentUser() || !task.getState().equals(TaskState.OPEN) || task.getCandidates().isEmpty()) {
            return "permission-denied";
        }

        model.addAttribute("priceService", suggestedPriceService);
        model.addAttribute("task", task);

        return "candidates";
    }

    @PostMapping("/candidates/{id}/{username}")
    public String chooseCandidate(Model model, Task task, BindingResult result, @PathVariable("id") BigInteger id, @PathVariable String username) {

        Task taskById = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        if (userService.findByUsername(username) == null) {
            return "no-user-err";
        }

        User user = userService.findByUsername(username);


//todo: provide a better solution.
        task.setCandidates(taskById.getCandidates());
        task.setSuggestedPrices(taskById.getSuggestedPrices());
        taskValidator.validateCandidate(task, user, result);
        if(result.hasErrors()){
            model.addAttribute("task", task);
            model.addAttribute("priceService", suggestedPriceService);
            return "candidates";
        }

        SuggestedPrice suggestedPrice = suggestedPriceService.getSuggestedPrice(user, taskById);

        user.confirmTask(taskById, suggestedPrice.getValue());
        taskService.save(taskById);

        return "redirect:/viewtask/{id}";
    }

    @GetMapping("/history/{id}")
    public String viewTaskHistory(Model model, @PathVariable("id") BigInteger id){
        Task task = taskService.findById(id);
        if (task == null) {
            return "no-task-err";
        }

        User user = userService.getCurrentUser();

        if (task.getOwner() != user && task.getManager() != user) { //&& !user.getRoles().contains(Roles.ADMIN)) {
            return "permission-denied";
        }

        model.addAttribute("task", task);
        return "view-task-history";
    }
}
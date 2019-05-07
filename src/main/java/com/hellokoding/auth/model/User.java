package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER")
public class User {
    //todo:equals, hashcode
    @Id
    @Column(name = "ID_USER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    private Integer balance = 0;

    private Integer reserved = 0;

    private String sumBuff;

    @Transient
    private String passwordConfirm;

    @ManyToMany
    private Set<Role> roles = new HashSet<>();

    public User() {
    }

    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<Task> createdTasks = new HashSet<>();

    @OneToMany(mappedBy = "executor")
    private Set<Task> assignedTasks = new HashSet<>();

    @ManyToMany(mappedBy = "candidates")
    private Set<Task> acceptedTasks = new HashSet<>();

    @OneToMany
    private Set<SuggestedPrice> suggestedPrices = new HashSet<>();

    public void createTask(Task task) {
        this.balance -= task.getPrice();
        this.reserved += task.getPrice();
        task.setOwner(this);
        task.setState(TaskState.OPEN);
    }

    public void changeTask(Task originalTask, Task task) {
        this.changeTaskPrice(originalTask, task.getPrice());
        originalTask.changeAllAttributes(task);
    }

    public void changeTaskPrice(Task task, Integer price) {
        Integer priceDifference = task.getPrice() - price;

        this.reserved = this.reserved - priceDifference;
        this.balance = this.balance + priceDifference;
    }

    public void confirmTask(Task task, Integer price) {
        task.getOwner().changeTaskPrice(task, price);
        task.setExecutor(this);
        task.setState(TaskState.ASSIGNED);
        task.setCandidates(null);
        task.setPrice(price);
    }

    public void addTaskToCandidates(Task task) {
        task.addCandidate(this);
    }

    public void removeTaskFromCandidates(Task task) {
        task.removeCandidate(this);
    }

    public void withdrawMoney(Integer sum) {
        if (sum < this.balance)
            this.balance -= sum;
    }

    public void retMoneyForTask(Integer price) {
        reserved -= price;
        balance += price;
    }

    public void addRole(String role) {
        this.roles.add(new Role(role));
    }

    public void sendMoneyTo(User executor, Integer price) {
        reserved -= price;
        executor.setBalance(executor.getBalance() + price);
    }

    public Float getOwnerRating() {
        Float sum = (float) 0;
        int i = 0;
        for (Task task : createdTasks) {
            if (task.getState().equals(TaskState.FINISHED) && task.getOwnerRating() != null) {
                sum += task.getOwnerRating().getRating();
                i++;
            }
        }
        if (i != 0)
            return sum / i;
        else return (float) 0;
    }

    public Float getExecutorRating() {
        Float sum = (float) 0;
        int i = 0;
        for (Task task : assignedTasks) {
            if (task.getState().equals(TaskState.FINISHED)) {
                sum += task.getExecutorRating().getRating();
                i++;
            }
        }
        if (i != 0)
            return sum / i;
        else return (float) 0;
    }

    public Set<Task> getAcceptedTasks() {
        return acceptedTasks;
    }

    public Set<Task> getCreatedTasks() {
        return createdTasks;
    }

    public Set<Task> getAssignedTasks() {
        return assignedTasks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getReserved() {
        return reserved;
    }

    public void setReserved(Integer reserved) {
        this.reserved = reserved;
    }

    public Set<SuggestedPrice> getSuggestedPrices() {
        return suggestedPrices;
    }

    public void setSuggestedPrices(Set<SuggestedPrice> suggestedPrices) {
        this.suggestedPrices = suggestedPrices;
    }

    public String getSumBuff() {
        return sumBuff;
    }

    public void setSumBuff(String sumBuff) {
        this.sumBuff = sumBuff;
    }
}

package com.hellokoding.auth.model;

import javax.persistence.*;

@Entity
@Table(name = "SUGGESTED_PRICE")
public class SuggestedPrice {

    @Id
    @Column(name = "ID_SUGGESTED_PRICE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinTable(name = "USER_SUGGESTEDPRICE", joinColumns = @JoinColumn(name = "suggested_PRICE", referencedColumnName = "ID_SUGGESTED_PRICE"),
//            inverseJoinColumns = @JoinColumn(name = "who_suggested", referencedColumnName = "ID_USER"))
    private User suggester;

    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinTable(name = "TASK_SUGGESTEDPRICE", joinColumns = @JoinColumn(name = "suggested_PRICE", referencedColumnName = "ID_SUGGESTED_PRICE"),
//            inverseJoinColumns = @JoinColumn(name = "task_suggested", referencedColumnName = "ID_TASK"))
    private Task candidateTask;

    public SuggestedPrice(Task task, User user){
        this.suggester = user;
        this.candidateTask = task;
        this.value = task.getPrice();
    }

    public SuggestedPrice() {
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSuggester() {
        return suggester;
    }

    public void setSuggester(User suggester) {
        this.suggester = suggester;
    }

    public Task getCandidateTask() {
        return candidateTask;
    }

    public void setCandidateTask(Task candidateTask) {
        this.candidateTask = candidateTask;
    }
}

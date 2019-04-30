package com.hellokoding.auth.model;


import javax.persistence.*;

@Entity
@Table(name = "RATING")
public class Rating {

    @Id
    @Column(name = "ID_RATING")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Integer ratingNum;


    @ManyToOne(fetch = FetchType.LAZY)
    private Task task;

    private String comment;

    private String author;

    public Rating() {}

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Boolean isOfOwner() {
        return task.getOwner().getUsername().equals(author);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRatingNum() {
        return ratingNum;
    }

    public void setRatingNum(Integer ratingNum) {
        this.ratingNum = ratingNum;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}

package com.hellokoding.auth.model;


import javax.persistence.*;

@Entity
@Table(name = "RATING")
public class Rating {

    @Id
    @Column(name = "ID_RATING")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Float rating;
    
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

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
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

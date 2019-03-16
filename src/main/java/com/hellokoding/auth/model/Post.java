package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "POST")
public class Post {

    @Id
    @Column(name = "ID_POST")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String subject;

    private String description;

    private String text;

    private boolean accepted;

//    @OneToMany(fetch = FetchType.LAZY)
//    @JoinTable(name = "POST_CANDIDATES", joinColumns = @JoinColumn(name = "which_post", referencedColumnName = "ID_POST"),
//            inverseJoinColumns = @JoinColumn (name = "candidate", referencedColumnName = "ID_USER"))
//    private List<User> candidates;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_CREATEDPOST", joinColumns = @JoinColumn(name = "created_POST", referencedColumnName = "ID_POST"),
            inverseJoinColumns = @JoinColumn(name = "who_created", referencedColumnName = "ID_USER"))
    private User owner;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ACCEPTEDPOST", joinColumns = @JoinColumn(name = "accepted_POST", referencedColumnName = "ID_POST"),
            inverseJoinColumns = @JoinColumn(name = "who_accepted_USER", referencedColumnName = "ID_USER"))
    private User manager;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }


    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

//    public List<User> getCandidates() {
//        return candidates;
//    }
//
//    public void setCandidates(List<User> candidates) {
//        this.candidates = candidates;
//    }
}


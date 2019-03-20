package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    private Integer price = 0;



    private boolean confirmed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_CREATEDPOST", joinColumns = @JoinColumn(name = "created_POST", referencedColumnName = "ID_POST"),
            inverseJoinColumns = @JoinColumn(name = "who_created", referencedColumnName = "ID_USER"))
    private User owner;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_ACCEPTEDPOST", joinColumns = @JoinColumn(name = "accepted_POST", referencedColumnName = "ID_POST"),
            inverseJoinColumns = @JoinColumn(name = "who_accepted_USER", referencedColumnName = "ID_USER"))
    private User manager;


    @ManyToMany
    private Set<User> candidates;


    public void addCandidate(User user){
        if (candidates == null)
            candidates = new HashSet<>();
        candidates.add(user);
    }

    public void removeCandidate(User user){
        if (candidates != null)
            candidates.remove(user);
    }

    //ЖЕСТКИЙ ГОВНОКОД, НО КАК ЛУЧШЕ НЕ ЕБУ
    public boolean hasCandidate(String username){
        for (Iterator<User> candidate = this.getCandidates().iterator(); candidate.hasNext();){
            User user = candidate.next();
            if (user.getUsername().equals(username)){
                return true;
            }
        }
        return false;
    }

    public void changeAllAttributes(Post otherPost) {
        this.subject = otherPost.getSubject();
        this.description = otherPost.getDescription();
        this.text = otherPost.getText();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) { this.owner = owner; }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }

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

    public Set<User> getCandidates() { return candidates; }

    public void setCandidates(Set<User> candidates) {
        this.candidates = candidates;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}


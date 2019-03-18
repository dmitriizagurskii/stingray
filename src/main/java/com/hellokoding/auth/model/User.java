package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {

    @Id
    @Column(name = "ID_USER")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String email;

    private String password;

    @Transient
    private String passwordConfirm;

    @ManyToMany
    private Set<Role> roles;

    public User() {
    }

    @OneToMany
    @JoinTable(name = "USER_CREATEDPOST", joinColumns = @JoinColumn(name = "who_created", referencedColumnName = "ID_USER"),
            inverseJoinColumns = @JoinColumn(name = "created_POST", referencedColumnName = "ID_POST"))
    private Set<Post> createdPosts;

    @OneToMany
    @JoinTable(name = "USER_ACCEPTEDPOST", joinColumns = @JoinColumn(name = "who_accepted_USER", referencedColumnName = "ID_USER"),
            inverseJoinColumns = @JoinColumn(name = "accepted_POST", referencedColumnName = "ID_POST"))
    private Set<Post> acceptedPosts;

    @ManyToMany(mappedBy="candidates")
    private Set<Post> candidatePosts;


    public void addPost(Post post) {
        if (createdPosts == null)
            createdPosts = new HashSet<>();

        post.setOwner(this);
    }

    public void acceptPost(Post post) {
        if (acceptedPosts == null)
            acceptedPosts = new HashSet<>();

        post.setManager(this);
        post.setAccepted(true);
    }

    public void addPostToCandidates(Post post) {

        if (candidatePosts == null)
            candidatePosts = new HashSet<>();

        candidatePosts.add(post);
        if (post.getCandidates() == null)
            post.setCandidates(new HashSet<>());
        post.getCandidates().add(this);
    }

    public void deletePostFromCandidates(Post post) {
        candidatePosts.remove(post);
        post.getCandidates().remove(this);
    }


    public Set<Post> getCandidatePosts() {
        return candidatePosts;
    }

    public Set<Post> getCreatedPosts() {
        return createdPosts;
    }

    public Set<Post> getAcceptedPosts() {
        return acceptedPosts;
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
}

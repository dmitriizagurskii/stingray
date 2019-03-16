package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.ArrayList;
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
            inverseJoinColumns = @JoinColumn (name = "created_POST", referencedColumnName = "ID_POST"))
    private List<Post> createdPosts ;

    @OneToMany
    @JoinTable(name = "USER_ACCEPTEDPOST", joinColumns = @JoinColumn(name = "who_accepted_USER", referencedColumnName = "ID_USER"),
            inverseJoinColumns = @JoinColumn (name = "accepted_POST", referencedColumnName = "ID_POST"))
    private List<Post> acceptedPosts;


    public List<Post> getCreatedPosts() {
        return createdPosts;
    }

    public List<Post> getAcceptedPosts() {
        return acceptedPosts;
    }

    public void addPost (Post post)  {
        if (createdPosts == null)
            createdPosts = new ArrayList<>();

        post.setOwner(this);
    }

    public void acceptPost (Post post) {
        if (acceptedPosts == null)
            acceptedPosts = new ArrayList<>();

        post.setManager(this);
        post.setAccepted(true);
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

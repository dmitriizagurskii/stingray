package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
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

    @ManyToMany(mappedBy = "candidates")
    private Set<Post> candidatePosts;


    public void createPost(Post post) {
        if (createdPosts == null)
            createdPosts = new HashSet<>();
        this.balance -= post.getPrice();
        this.reserved += post.getPrice();
        post.setOwner(this);
    }

    public void changePost(Post originalPost, Post post) {
        Integer priceDifference = originalPost.getPrice() - post.getPrice();

        this.reserved = this.reserved - priceDifference;
        this.balance = balance + priceDifference;

        originalPost.changeAllAttributes(post);
    }

    public void confirmPost(Post post) {
        if (acceptedPosts == null)
            acceptedPosts = new HashSet<>();

        post.setManager(this);
        post.setConfirmed(true);
        post.setCandidates(null);
    }

    public void addPostToCandidates(Post post) {

        if (candidatePosts == null)
            candidatePosts = new HashSet<>();

        post.addCandidate(this);
    }

    public void removePostFromCandidates(Post post) {
        if (candidatePosts != null) {
            candidatePosts.remove(post);
        }
        post.removeCandidate(this);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
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

    public void withdrawMoney(Integer sum) {
        if (sum < this.balance)
            this.balance -= sum;
    }
}

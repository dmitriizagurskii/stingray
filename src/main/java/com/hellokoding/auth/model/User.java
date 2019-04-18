package com.hellokoding.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
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
    private Set<Role> roles;

    public User() {
    }

    @OneToMany(mappedBy = "owner", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<Post> createdPosts;

    @OneToMany(mappedBy = "manager")
    private Set<Post> acceptedPosts;

    @ManyToMany(mappedBy = "candidates")
    private Set<Post> candidatePosts;

    @OneToMany
    private Set<SuggestedPrice> suggestedPrices;

    public void createPost(Post post) {
        if (createdPosts == null)
            createdPosts = new HashSet<>();
        this.balance -= post.getPrice();
        this.reserved += post.getPrice();
        post.setOwner(this);
        post.setState(PostState.OPEN);
    }

    public void changePost(Post originalPost, Post post) {
        this.changePostPrice(originalPost, post.getPrice());
        originalPost.changeAllAttributes(post);
    }

    public void changePostPrice(Post post, Integer price){
        Integer priceDifference = post.getPrice() - price;

        this.reserved = this.reserved - priceDifference;
        this.balance = this.balance + priceDifference;
    }

    public void confirmPost(Post post, Integer price) {
        if (acceptedPosts == null)
            acceptedPosts = new HashSet<>();

        post.getOwner().changePostPrice(post, price);
        post.setManager(this);
        post.setState(PostState.ASSIGNED);
        post.setCandidates(null);
        post.setPrice(price);
    }

    public void addPostToCandidates(Post post) {

        if (candidatePosts == null)
            candidatePosts = new HashSet<>();
        if (suggestedPrices == null)
            suggestedPrices = new HashSet<>();
        post.addCandidate(this);
    }

    public void removePostFromCandidates(Post post) {
        if (candidatePosts != null) {
            candidatePosts.remove(post);
        }
        post.removeCandidate(this);
    }

    public void withdrawMoney(Integer sum) {
        if (sum < this.balance)
            this.balance -= sum;
    }

    public void retMoneyForPost (Integer price) {
        reserved -= price;
        balance += price;
    }

    public void addRole(String role){
        if (roles == null) {
            roles = new HashSet<>();
        }
        this.roles.add(new Role(role));
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

package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "SUGGESTED_PRICE")
public class SuggestedPrice {

    @Id
    @Column(name = "ID_SUGGESTED_PRICE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "USER_SUGGESTEDPRICE", joinColumns = @JoinColumn(name = "suggested_PRICE", referencedColumnName = "ID_SUGGESTED_PRICE"),
            inverseJoinColumns = @JoinColumn(name = "who_suggested", referencedColumnName = "ID_USER"))
    private User suggester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "POST_SUGGESTEDPRICE", joinColumns = @JoinColumn(name = "suggested_PRICE", referencedColumnName = "ID_SUGGESTED_PRICE"),
            inverseJoinColumns = @JoinColumn(name = "post_suggested", referencedColumnName = "ID_POST"))
    private Post candidatePost;

    public SuggestedPrice(Post post, User user){
        this.suggester = user;
        this.candidatePost = post;
        this.value = post.getPrice();
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

    public Post getCandidatePost() {
        return candidatePost;
    }

    public void setCandidatePost(Post candidatePost) {
        this.candidatePost = candidatePost;
    }
}

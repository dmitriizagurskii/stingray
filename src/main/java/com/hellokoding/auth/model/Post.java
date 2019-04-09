package com.hellokoding.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hellokoding.auth.service.DateService;

import javax.persistence.*;
import java.text.ParseException;
import java.util.*;
import java.util.zip.DataFormatException;

@Entity
@Table(name = "POST")
@JsonIgnoreProperties(value = {"owner", "manager", "candidates", "suggestedPrices", "postFiles", "chatMessages"})
public class Post {
//todo:equals, hashcode

    @Id
    @Column(name = "ID_POST")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String subject;

    private String description;

    private String text;

    private Integer price = 0;

    private boolean confirmed;

    private Calendar deadline;

    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private User manager;

    @ManyToMany
    private Set<User> candidates;

    @OneToMany(mappedBy = "candidatePost", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<SuggestedPrice> suggestedPrices;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<PostFile> postFiles;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public void addCandidate(User user) {
        if (candidates == null)
            candidates = new HashSet<>();
        candidates.add(user);
    }

    public void removeCandidate(User user) {
        candidates.remove(user);
    }

    public void changeAllAttributes(Post otherPost) {
        this.subject = otherPost.getSubject();
        this.description = otherPost.getDescription();
        this.text = otherPost.getText();
        this.price = otherPost.getPrice();
    }

    public void addPostFile(PostFile postFile){
        if (postFiles == null)
            postFiles = new HashSet<>();
        postFiles.add(postFile);
    }

    public void changePrice(Post post){

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

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
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

    public Set<User> getCandidates() {
        return candidates;
    }

    public void setCandidates(Set<User> candidates) {
        this.candidates = candidates;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Set<SuggestedPrice> getSuggestedPrices() {
        return suggestedPrices;
    }

    public void setSuggestedPrices(Set<SuggestedPrice> suggestedPrices) {
        this.suggestedPrices = suggestedPrices;
    }

    public Set<PostFile> getPostFiles() {
        return postFiles;
    }

    public void setPostFiles(Set<PostFile> postFiles) {
        this.postFiles = postFiles;
    }

    public void setDeadline(int year, int month, int day, int hours, int minutes) {
        deadline = new GregorianCalendar(year, month, day, hours, minutes);
    }

    public Calendar getDeadline() {
        return deadline;
    }

    public boolean isExpired() {
        Date currentDate = Calendar.getInstance().getTime();
        return (currentDate.after(deadline.getTime()));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDeadline() throws ParseException {
        deadline = DateService.convertToCalendar(date);
    }

    public String getTimeLeft() {
        return DateService.getDateDiff(deadline);
    }

    public String getDeadlineStr() {
        return DateService.viewDate(deadline);
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }
}


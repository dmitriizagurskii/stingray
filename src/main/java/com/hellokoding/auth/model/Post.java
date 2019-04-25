package com.hellokoding.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hellokoding.auth.service.DateService;

import javax.persistence.*;
import java.text.ParseException;
import java.util.*;

@Entity
@Table(name = "POST")
@JsonIgnoreProperties(value = {"subject", "description", "text", "price", "state", "deadline", "date",
        "owner", "manager", "candidates", "suggestedPrices", "postFiles", "chatMessages", "timeLeft", "expired", "deadlineStr"})
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PostState state;

    private Calendar deadline;

    private String date;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private User manager;

    @ManyToMany
    private Set<User> candidates = new HashSet<>();

    @OneToMany(mappedBy = "candidatePost", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<SuggestedPrice> suggestedPrices;

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<PostFile> postFiles = new HashSet<>();

    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<ChatMessage> chatMessages = new ArrayList<>();


    @OneToMany(mappedBy = "post", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Rating> ratingList;


    public boolean rate (Rating rating) {

        if (ratingList.size() > 1)
            return false;
        else
            ratingList.add(rating);
        return true;
    }


    public Rating getRatingOfOwner() {
        Rating rating = null;
        for (Rating currRating: ratingList) {
            if (currRating.isOfOwner())
                rating = currRating;
        }
        return rating;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }


    public Rating getRatingOfManager() {
        Rating rating = null;
        for (Rating currRating: ratingList) {
            if (!currRating.isOfOwner())
                rating = currRating;
        }
        return rating;
    }


    public void addCandidate(User user) {
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
        this.date = otherPost.getDate();
        this.state = PostState.OPEN;
        try {
            setDeadline();
        } catch (Exception e) {
            System.out.println("OHH YEAAA");
        }
    }

    public void addPostFile(PostFile postFile) {
        postFile.setPost(this);
    }

    public void checkExpired() {
        Date currentDate = Calendar.getInstance().getTime();
        if (currentDate.after(deadline.getTime())) {
            switch (state) {
                case OPEN:
                    state = PostState.EXPIRED;
                    candidates.clear();
                    owner.retMoneyForPost(this.getPrice());
                    return;
                case READY:
                    finish();
                    break;
                case ASSIGNED:
                    state = PostState.IN_DISPUTE;
                    break;

            }
        }
    }

    public Integer findLowestSuggestedPrice() {
        Integer min = this.price;
        if (suggestedPrices != null) {
            for (SuggestedPrice suggestedPrice : suggestedPrices) {
                Integer newMin = suggestedPrice.getValue();
                if (min > newMin)
                    min = newMin;
            }
        }
        return min;
    }

    public void changePrice(Post post) {

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

    public PostState getState() {
        return state;
    }

    public void setState(PostState state) {
        this.state = state;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDeadline() throws ParseException {
        deadline = DateService.convertToCalendar(date);
    }

    public long getDeadlineTime() {
        return deadline.getTime().getTime();
    }

    public String getTimeleft() {
        long timeleft = getDeadlineTime() - new Date().getTime();
        long days = timeleft / (1000 * 60 * 60 * 24);
        long hours = (timeleft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (timeleft % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (timeleft % (1000 * 60)) / 1000;
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
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

    public void finish() {
        state = PostState.FINISHED;
        this.owner.sendMoneyTo(this.manager, price);
    }
}


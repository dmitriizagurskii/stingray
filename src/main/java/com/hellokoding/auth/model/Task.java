package com.hellokoding.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hellokoding.auth.service.DateService;

import javax.persistence.*;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.*;

@Entity
@Table(name = "TASK")
@JsonIgnoreProperties(value = {"subject", "description", "text", "price", "state", "deadline", "date",
        "owner", "manager", "candidates", "suggestedPrices", "taskFiles", "chatMessages", "timeLeft",
        "expired", "deadlineStr", "ratingList", "ratingOfOwner", "ratingOfManager"})
public class Task implements Cloneable {
//todo:equals, hashcode

    @Id
    @Column(name = "ID_TASK")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private BigInteger id;

    private String subject;

    private String description;

    @Lob
    private String text;

    private Integer price = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TaskState state;

    private Calendar deadline;

    private String date;

    private Date disputeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    private User manager;

    @ManyToMany
    private Set<User> candidates = new HashSet<>();

    @OneToMany(mappedBy = "candidateTask", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<SuggestedPrice> suggestedPrices;

    @OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private Set<TaskFile> taskFiles = new HashSet<>();

    @OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<ChatMessage> chatMessages = new ArrayList<>();


    @OneToMany(mappedBy = "task", orphanRemoval = true, cascade = CascadeType.MERGE)
    private List<Rating> ratingList;


    public boolean rate(Rating rating) {

        if (ratingList.size() > 1)
            return false;
        else
            ratingList.add(rating);
        return true;
    }


    public Rating getRatingOfOwner() {
        Rating rating = null;
        for (Rating currRating : ratingList) {
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
        for (Rating currRating : ratingList) {
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

    public void changeAllAttributes(Task otherTask) {
        this.subject = otherTask.getSubject();
        this.description = otherTask.getDescription();
        this.text = otherTask.getText();
        this.price = otherTask.getPrice();
        this.date = otherTask.getDate();
        this.state = TaskState.OPEN;
        try {
            setDeadline();
        } catch (Exception e) {
            System.out.println("OHH YEAAA");
        }
    }

    public void addTaskFile(TaskFile taskFile) {
        taskFile.setTask(this);
    }

    public void checkExpired() {
        Date currentDate = Calendar.getInstance().getTime();
        if (currentDate.after(deadline.getTime())) {
            switch (state) {
                case OPEN:
                    state = TaskState.EXPIRED;
                    candidates.clear();
                    owner.retMoneyForTask(this.getPrice());
                    return;
                case READY:
                    finish();
                    break;
                case ASSIGNED:
                    state = TaskState.IN_DISPUTE;
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

    public String getTimeleftStr() {
        long timeleft = getDeadlineTime() - new Date().getTime();
        long days = timeleft / (1000 * 60 * 60 * 24);
        long hours = (timeleft % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (timeleft % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (timeleft % (1000 * 60)) / 1000;
        return days + "d " + hours + "h " + minutes + "m " + seconds + "s";
    }

    public long getTimeleft() {
        return getDeadlineTime() - new Date().getTime();
    }

    public void finish() {
        state = TaskState.FINISHED;
        this.owner.sendMoneyTo(this.manager, price);
    }

    public void changePrice(Task task) {

    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
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

    public TaskState getState() {
        return state;
    }

    public void setState(TaskState state) {
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

    public Set<TaskFile> getTaskFiles() {
        return taskFiles;
    }

    public void setTaskFiles(Set<TaskFile> taskFiles) {
        this.taskFiles = taskFiles;
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

    public String getDeadlineStr() {
        return DateService.viewDate(deadline);
    }

    public List<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public Date getDisputeDate() {
        return disputeDate;
    }

    public void setDisputeDate(Date disputeDate) {
        this.disputeDate = disputeDate;
    }

    @Lob
    private String log = "";
    @Transient
    private Task previousTask;

    @PrePersist
    public void createdTask() {
        log = new Date().toString() + "\tTask created\n";
    }

    @PostLoad
    public void saveUnchangedTask() throws CloneNotSupportedException {
        previousTask = (Task) this.clone();
    }

    @PreUpdate
    public void checkForChanges() {
        this.compareTo(previousTask);
        System.out.println(log);
    }

    private void compareTo(Task task) {
        StringBuilder sb = new StringBuilder(this.log);
        if (!this.subject.equals(task.subject)) {
            sb.append(new Date().toString());
            sb.append("\tSubject changed from ");
            sb.append(task.subject);
            sb.append(" to ");
            sb.append(this.subject);
            sb.append("\n");
        }

        this.log += sb.toString();
    }

}


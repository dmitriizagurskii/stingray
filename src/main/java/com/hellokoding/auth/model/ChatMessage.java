package com.hellokoding.auth.model;

import javax.persistence.*;
import java.util.Date;

@Entity
public class ChatMessage {

    @Id
    @Column(name="ID_CHATMESSAGE")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private MessageType type;

    private String content;

    private String senderUsername;

    private Date date;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setSender(User sender) {
        this.sender = sender;
        this.senderUsername = this.sender.getUsername();
    }
}
package com.hellokoding.auth.model;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class ChatMessage {

    @Id
    @Column(name="ID_CHATMESSAGE")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private MessageType type;

    @Lob
    private String content;

    private String senderUsername;

    private Date date;

    private String taskId;

    @ManyToOne
    private Task task;

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

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String dateToString() {

//        var messageDate = new Date(date);
//        var currentDate = new Date();
//        if (messageDate.getFullYear() != currentDate.getFullYear()) {
//            return messageDate.getDate()+'.'+messageDate.getMonth()+'.'+messageDate.getFullYear();
//        } else if(messageDate.getDate() != currentDate.getDate()) {
//            return messageDate.toLocaleTimeString().replace(/:\d+ /, ' ')+' '+messageDate.getDate()+'.'+messageDate.getMonth();
//        } else {
//            return messageDate.toLocaleTimeString().replace(/:\d+ /, ' ');
//        }
        Date currentDate = new Date();
        SimpleDateFormat format;
        if (date.getTime() - currentDate.getTime() > 365*24*60*60) {
            format = new SimpleDateFormat("dd-MM-yyyy");
        } else if (date.getTime() - currentDate.getTime() > 24*60*60){
            format = new SimpleDateFormat("dd-MM h:mm a");
        } else {
            format = new SimpleDateFormat("h:mm a");
        }
        return format.format(date);
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
package com.hellokoding.auth.model;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class TaskFile {

    @Id
    @Column(name = "ID_FILE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    @ManyToOne
//    @JoinTable(name = "TASK_FILES", joinColumns = @JoinColumn(name = "file", referencedColumnName = "ID_FILE"),
//            inverseJoinColumns = @JoinColumn(name = "task", referencedColumnName = "ID_TASK"))
    private Task task;

    @Lob
    private byte[] data;

    public TaskFile() {
    }

    public TaskFile(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}

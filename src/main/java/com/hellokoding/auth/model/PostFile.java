package com.hellokoding.auth.model;

import com.hellokoding.auth.model.Post;

import javax.persistence.*;

@Entity
@Table(name = "files")
public class PostFile {

    @Id
    @Column(name = "ID_FILE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    @ManyToOne
    @JoinTable(name = "POST_FILES", joinColumns = @JoinColumn(name = "file", referencedColumnName = "ID_FILE"),
            inverseJoinColumns = @JoinColumn(name = "post", referencedColumnName = "ID_POST"))
    private Post post;

    @Lob
    private byte[] data;

    public PostFile() {
    }

    public PostFile(String fileName, String fileType, byte[] data) {
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

}

package com.hellokoding.auth.web;

import com.hellokoding.auth.model.Post;
import com.hellokoding.auth.service.PostService;
import com.hellokoding.auth.model.PostFile;
import com.hellokoding.auth.service.PostFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

    @Autowired
    PostFileService postFileService;

    @Autowired
    private PostService postService;

    @PostMapping(value = "/viewpost/{id}", params = "addfiles")
    public String uploadFile(@PathVariable("id") Long id, @RequestParam("files") MultipartFile[] files) {

        Post post = postService.findById(id);
        if (post == null) {
            return "no-post-err";
        }

        for (MultipartFile mf: files) {
            post.addPostFile(postFileService.save(postFileService.getPostFile(mf)));
        }

        postService.save(post);
        return "redirect:/viewpost/{id}";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        PostFile postFile = postFileService.findById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(postFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + postFile.getFileName() + "\"")
                .body(new ByteArrayResource(postFile.getData()));
    }
}

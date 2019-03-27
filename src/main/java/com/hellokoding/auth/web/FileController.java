package com.hellokoding.auth.web;

import com.hellokoding.auth.model.PostFile;
import com.hellokoding.auth.service.PostFileService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FileController {

    @Autowired
    PostFileService postFileService;

    @PostMapping("/deletefile/{id}")
    public String deleteFile(@RequestParam("fileId") Long fileId) {
        postFileService.deleteById(fileId);
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

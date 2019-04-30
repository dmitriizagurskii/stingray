package com.hellokoding.auth.web;

import com.hellokoding.auth.model.TaskFile;
import com.hellokoding.auth.service.TaskFileService;

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

@Controller
public class FileController {

    @Autowired
    TaskFileService taskFileService;

    @PostMapping("/deletefile/{id}")
    public String deleteFile(@RequestParam("fileId") Long fileId) {
        taskFileService.deleteById(fileId);
        return "redirect:/viewtask/{id}";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        TaskFile taskFile = taskFileService.findById(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(taskFile.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + taskFile.getFileName() + "\"")
                .body(new ByteArrayResource(taskFile.getData()));
    }
}

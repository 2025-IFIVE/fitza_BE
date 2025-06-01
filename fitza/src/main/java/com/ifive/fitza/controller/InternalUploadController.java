package com.ifive.fitza.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/internal")
@RequiredArgsConstructor
public class InternalUploadController {

    @PostMapping("/upload-cropped")
    public ResponseEntity<String> receiveCroppedImage(
            @RequestPart("file") MultipartFile file
    ) throws IOException {
       
        String saveDir = System.getProperty("user.dir") + "/uploads/cropped/";
        File dir = new File(saveDir);
        if (!dir.exists()) dir.mkdirs();

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        File targetFile = new File(saveDir + filename);
        file.transferTo(targetFile);

        return ResponseEntity.ok("/uploads/cropped/" + filename);
    }

    @PostMapping("/upload-original")
public ResponseEntity<String> receiveOriginalImage(@RequestPart("file") MultipartFile file) throws IOException {
    String saveDir = System.getProperty("user.dir") + "/uploads/original/";
    File dir = new File(saveDir);
    if (!dir.exists()) dir.mkdirs();

    String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
    File targetFile = new File(saveDir + filename);
    file.transferTo(targetFile);

    return ResponseEntity.ok("/uploads/original/" + filename);
}

}

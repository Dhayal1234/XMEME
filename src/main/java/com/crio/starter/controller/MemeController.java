package com.crio.starter.controller;

import com.crio.starter.data.Meme;
import com.crio.starter.exception.EmptyMemeDataException;
import com.crio.starter.service.MemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/memes")
public class MemeController {

    @Autowired
    private MemeService memeService;

    @PostMapping
    public ResponseEntity<Meme> createMeme(@RequestBody Meme meme) {
       
        if (meme == null || meme.getName() == null || meme.getCaption() == null || meme.getUrl() == null) {
            return ResponseEntity.badRequest().build();
        }

        Meme createdMeme = memeService.createMeme(meme);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMeme);
    }

    @GetMapping
    public ResponseEntity<List<Meme>> getAllMemes() {
        List<Meme> memes = memeService.getAllMemes();
        return new ResponseEntity<>(memes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Meme> getMemeById(@PathVariable String id) {
        Meme meme = memeService.getMemeById(id);
        return ResponseEntity.ok(meme);
    }
}


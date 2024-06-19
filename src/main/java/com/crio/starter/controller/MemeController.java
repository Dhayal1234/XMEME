package com.crio.starter.controller;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.service.MemeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memes")
public class MemeController {

  private final MemeService memeService;

  @PostMapping
  public ResponseEntity<Object> postMeme(@RequestBody MemeEntity memeEntity) {
    if (memeEntity.getName() == null || memeEntity.getName().isEmpty() ||
        memeEntity.getUrl() == null || memeEntity.getUrl().isEmpty() ||
        memeEntity.getCaption() == null || memeEntity.getCaption().isEmpty()) {
      return new ResponseEntity<>("All fields (name, url, caption) are required.", HttpStatus.BAD_REQUEST);
    }

    try {
      MemeEntity savedMeme = memeService.createMeme(memeEntity);
      System.out.print("Test message "+savedMeme.getId());
      return new ResponseEntity<>(savedMeme.getId(), HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
    }
  }

  @GetMapping
  public ResponseEntity<List<MemeEntity>> getLatestMemes() {
    List<MemeEntity> memes = memeService.getLatestMemes();
    if (memes.isEmpty()) {
      return new ResponseEntity<>(memes, HttpStatus.OK);
    }
    return new ResponseEntity<>(memes, HttpStatus.OK);
  }


  @GetMapping("/{id}")
  public ResponseEntity<Object> getMemeById(@PathVariable String id) {
    try {
      MemeEntity meme = memeService.getMemeById(id);
      return new ResponseEntity<>(meme, HttpStatus.OK);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
  }




}


package com.crio.starter.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.dto.MemeDto;
import com.crio.starter.repository.MemeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

public class MemeServiceTest {

  @Mock
  private MemeRepository memeRepository;

  @InjectMocks
  private MemeService memeService;

  public MemeServiceTest() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createMeme_duplicateMeme_throwsException() {
    MemeEntity meme = new MemeEntity();
    meme.setName("Test");
    meme.setUrl("http://test.com");
    meme.setCaption("Test caption");

    when(memeRepository.existsByNameAndUrlAndCaption(meme.getName(), meme.getUrl(), meme.getCaption())).thenReturn(true);

    assertThrows(IllegalArgumentException.class, () -> memeService.createMeme(meme));
  }

  // @Test
  // void getMemeById_memeExists_returnsMeme() {
  //   MemeDto meme = new MemeDto();
  //   meme.setId("1");
  //   meme.setName("Test");
  //   meme.setUrl("http://test.com");
  //   meme.setCaption("Test caption");

  //   when(memeRepository.findById("1")).thenReturn(Optional.of(meme));

  //   MemeDto foundMeme = memeService.getMemeById("1");
  //   assertEquals(meme, foundMeme);
  // }

  @Test
  void getMemeById_memeDoesNotExist_throwsException() {
    when(memeRepository.findById("1")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> memeService.getMemeById("1"));
  }
}


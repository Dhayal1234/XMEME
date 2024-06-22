package com.crio.starter.service;

import com.crio.starter.data.Meme;
import com.crio.starter.exception.MemeNotFoundException;
import com.crio.starter.repository.MemeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemeServiceTest {

    @Mock
    private MemeRepository memeRepository;

    @InjectMocks
    private MemeServiceImpl memeService;

    @Test
    void createMemeTest() {
        Meme meme = new Meme();
        meme.setName("John Doe");
        meme.setUrl("http://example.com/meme.jpg");
        meme.setCaption("Funny Meme");

        when(memeRepository.existsByNameAndUrlAndCaption(anyString(), anyString(), anyString())).thenReturn(false);
        when(memeRepository.save(any(Meme.class))).thenReturn(meme);

        Meme createdMeme = memeService.createMeme(meme);
        assertEquals(meme.getName(), createdMeme.getName());
        verify(memeRepository, times(1)).save(meme);
    }

    @Test
    void getAllMemesTest() {
        List<Meme> memes = List.of(new Meme(), new Meme());
        when(memeRepository.findAll()).thenReturn(memes);

        List<Meme> result = memeService.getAllMemes();
        assertEquals(2, result.size());
        verify(memeRepository, times(1)).findAll();
    }

    @Test
    void getMemeByIdTest() {
        Meme meme = new Meme();
        meme.setId("1");
        when(memeRepository.findById(anyString())).thenReturn(Optional.of(meme));

        Meme result = memeService.getMemeById("1");
        assertEquals(meme.getId(), result.getId());
        verify(memeRepository, times(1)).findById("1");
    }

    @Test
    void getMemeByIdNotFoundTest() {
        when(memeRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(MemeNotFoundException.class, () -> {
            memeService.getMemeById("1");
        });
        verify(memeRepository, times(1)).findById("1");
    }
}


package com.crio.starter.controller;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.service.MemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(MemeController.class)
public class MemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemeService memeService;

    @BeforeEach
    public void setup() {
        // Initialize mocks created above
        MockitoAnnotations.openMocks(this);
        // Set up the standalone configuration for the controller
        this.mockMvc = MockMvcBuilders.standaloneSetup(new MemeController(memeService)).build();
    }

    @Test
    public void testPostMeme_Success() throws Exception {
        MemeEntity memeEntity = new MemeEntity();
        memeEntity.setId("1");
        memeEntity.setName("John Doe");
        memeEntity.setCaption("This is a meme");
        memeEntity.setUrl("http://example.com/meme.jpg");

        when(memeService.createMeme(any(MemeEntity.class))).thenReturn(memeEntity);

        mockMvc.perform(post("/memes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"caption\":\"This is a meme\",\"url\":\"http://example.com/meme.jpg\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    public void testPostMeme_Failure_EmptyFields() throws Exception {
        mockMvc.perform(post("/memes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\",\"caption\":\"\",\"url\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("All fields (name, url, caption) are required."));
    }

    @Test
    public void testPostMeme_Failure_Duplicate() throws Exception {
        when(memeService.createMeme(any(MemeEntity.class))).thenThrow(new IllegalArgumentException("Duplicate meme"));

        mockMvc.perform(post("/memes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"John Doe\",\"caption\":\"This is a meme\",\"url\":\"http://example.com/meme.jpg\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Duplicate meme"));
    }

    @Test
    public void testGetLatestMemes_Success() throws Exception {
        MemeEntity meme1 = new MemeEntity();
        meme1.setId("1");
        meme1.setName("John Doe");
        meme1.setCaption("This is a meme");
        meme1.setUrl("http://example.com/meme1.jpg");

        MemeEntity meme2 = new MemeEntity();
        meme2.setId("2");
        meme2.setName("Jane Doe");
        meme2.setCaption("This is another meme");
        meme2.setUrl("http://example.com/meme2.jpg");

        List<MemeEntity> memes = Arrays.asList(meme1, meme2);
        when(memeService.getLatestMemes()).thenReturn(memes);

        mockMvc.perform(get("/memes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":\"1\",\"name\":\"John Doe\",\"caption\":\"This is a meme\",\"url\":\"http://example.com/meme1.jpg\"}," +
                        "{\"id\":\"2\",\"name\":\"Jane Doe\",\"caption\":\"This is another meme\",\"url\":\"http://example.com/meme2.jpg\"}]"));
    }

    @Test
    public void testGetMemeById_Success() throws Exception {
        MemeEntity meme = new MemeEntity();
        meme.setId("1");
        meme.setName("John Doe");
        meme.setCaption("This is a meme");
        meme.setUrl("http://example.com/meme.jpg");

        when(memeService.getMemeById(anyString())).thenReturn(meme);

        mockMvc.perform(get("/memes/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":\"1\",\"name\":\"John Doe\",\"caption\":\"This is a meme\",\"url\":\"http://example.com/meme.jpg\"}"));
    }

    @Test
    public void testGetMemeById_NotFound() throws Exception {
        when(memeService.getMemeById(anyString())).thenThrow(new IllegalArgumentException("Meme not found"));

        mockMvc.perform(get("/memes/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Meme not found"));
    }
}

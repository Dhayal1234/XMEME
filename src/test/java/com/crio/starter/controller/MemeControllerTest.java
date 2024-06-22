package com.crio.starter.controller;

import com.crio.starter.data.Meme;
import com.crio.starter.repository.MemeRepository;
import com.crio.starter.service.MemeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemeController.class)
public class MemeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemeService memeService;

    @Autowired
    private MemeRepository memeRepository;

    @BeforeEach
    public void setUp() {
        // Clean up the database before each test
        memeRepository.deleteAll();
    }

    @Test
    void createMemeTest() throws Exception {
        Meme meme = new Meme();
        meme.setName("John Doe");
        meme.setUrl("http://example.com/meme.jpg");
        meme.setCaption("Funny Meme");

        when(memeService.createMeme(any(Meme.class))).thenReturn(meme);

        mockMvc.perform(post("/memes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"John Doe\", \"url\": \"http://example.com/meme.jpg\", \"caption\": \"Funny Meme\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void getAllMemesTest() throws Exception {
        Meme meme = new Meme();
        meme.setName("John Doe");
        meme.setUrl("http://example.com/meme.jpg");
        meme.setCaption("Funny Meme");

        when(memeService.getAllMemes()).thenReturn(List.of(meme));

        mockMvc.perform(get("/memes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getMemeByIdTest() throws Exception {
        Meme meme = new Meme();
        meme.setName("John Doe");
        meme.setUrl("http://example.com/meme.jpg");
        meme.setCaption("Funny Meme");

        when(memeService.getMemeById("1")).thenReturn(meme);

        mockMvc.perform(get("/memes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    public void testEmptyPostRequestReturnsBadRequest() throws Exception {
        String emptyRequestBody = "{}";

        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.post("/memes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyRequestBody));

        result.andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    
    
}


package com.crio.starter.service;

import com.crio.starter.exchange.ResponseDto;
import com.crio.starter.repository.MemeRepository;
import com.crio.starter.data.Meme;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

public interface MemeService {
    Meme createMeme(Meme meme);
    List<Meme> getAllMemes();
    Meme getMemeById(String id);
}

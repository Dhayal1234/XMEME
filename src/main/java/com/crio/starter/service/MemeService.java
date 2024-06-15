package com.crio.starter.service;

import com.crio.starter.data.MemeEntity;
import com.crio.starter.repository.MemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemeService {

  private final MemeRepository memeRepository;

  public MemeEntity createMeme(MemeEntity memeEntity) {
    if (memeRepository.existsByNameAndUrlAndCaption(memeEntity.getName(), memeEntity.getUrl(), memeEntity.getCaption())) {
      throw new IllegalArgumentException("Duplicate meme");
    }
    return memeRepository.save(memeEntity);
  }

  public List<MemeEntity> getLatestMemes() {
    return memeRepository.findAll(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "timestamp")))
        .getContent();
  }

  public MemeEntity getMemeById(String id) {
    return memeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Meme not found"));
  }
}

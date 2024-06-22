package com.crio.starter.service;

import com.crio.starter.exception.MemeNotFoundException;
import com.crio.starter.exception.DuplicateMemeException;
import com.crio.starter.data.Meme;
import com.crio.starter.repository.MemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemeServiceImpl implements MemeService {

    @Autowired
    private MemeRepository memeRepository;

    @Override
    public Meme createMeme(Meme meme) {
        if (memeRepository.existsByNameAndUrlAndCaption(meme.getName(), meme.getUrl(), meme.getCaption())) {
            throw new DuplicateMemeException("Duplicate meme found");
        }
        return memeRepository.save(meme);
    }

    @Override
    public List<Meme> getAllMemes() {
        // Sort by _id in descending order and limit to 100
        Sort sort = Sort.by(Sort.Direction.DESC, "_id");
        PageRequest pageRequest = PageRequest.of(0, 100, sort);
        return memeRepository.findAll(pageRequest).getContent();
    }


    @Override
    public Meme getMemeById(String id) {
        return memeRepository.findById(id)
                .orElseThrow(() -> new MemeNotFoundException("Meme not found with id: " + id));
    }

    
}

package com.crio.starter.repository;

import java.util.Optional;
import com.crio.starter.data.Meme;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeRepository extends MongoRepository<Meme, String> {
    boolean existsByNameAndUrlAndCaption(String name, String url, String caption);

    Optional<Meme> findByNameAndCaptionAndUrl(String name, String caption, String url);
}

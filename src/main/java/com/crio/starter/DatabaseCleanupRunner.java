package com.crio.starter;

import com.crio.starter.repository.MemeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleanupRunner implements CommandLineRunner {

    @Autowired
    private MemeRepository memeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Clean up the database at startup
        memeRepository.deleteAll();
    }
}


package br.com.advec.connect.scoreboard;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroupSeeder {

    @Bean
    CommandLineRunner seedGroups(ScoreGroupRepository repository) {
        return args -> {
            if (!repository.existsById("os-300")) {
                repository.save(new ScoreGroup("os-300", "Os 300"));
            }
            if (!repository.existsById("level-up")) {
                repository.save(new ScoreGroup("level-up", "Level Up"));
            }
        };
    }
}

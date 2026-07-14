package br.com.advec.connect.mission;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MissionSeeder {

    static final List<String> MISSION_IDS = List.of(
        "recrutar-participantes",
        "criar-identidade",
        "video-apresentacao",
        "completar-metas"
    );

    static final List<String> GROUP_IDS = List.of("level-up", "os-300");

    @Bean
    CommandLineRunner seedMissions(MissionCompletionRepository repository) {
        return args -> {
            for (String missionId : MISSION_IDS) {
                for (String groupId : GROUP_IDS) {
                    if (!repository.existsByMissionIdAndGroupId(missionId, groupId)) {
                        repository.save(new MissionCompletion(missionId, groupId, false));
                    }
                }
            }
        };
    }
}

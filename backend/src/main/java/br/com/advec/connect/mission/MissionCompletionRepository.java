package br.com.advec.connect.mission;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MissionCompletionRepository extends JpaRepository<MissionCompletion, Long> {
    List<MissionCompletion> findAll();
    Optional<MissionCompletion> findByMissionIdAndGroupId(String missionId, String groupId);
    boolean existsByMissionIdAndGroupId(String missionId, String groupId);
}

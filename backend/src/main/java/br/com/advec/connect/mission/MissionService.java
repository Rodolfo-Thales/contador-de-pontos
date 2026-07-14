package br.com.advec.connect.mission;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.advec.connect.mission.MissionDtos.MissionCompletionDto;

@Service
public class MissionService {

    private final MissionCompletionRepository repository;

    public MissionService(MissionCompletionRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MissionCompletionDto> getAll() {
        return repository.findAll().stream().map(MissionCompletionDto::from).toList();
    }

    @Transactional
    public MissionCompletionDto setCompletion(String missionId, String groupId, boolean completed) {
        MissionCompletion entity = repository.findByMissionIdAndGroupId(missionId, groupId)
            .orElseThrow(() -> new MissionNotFoundException(missionId, groupId));
        entity.setCompleted(completed);
        return MissionCompletionDto.from(entity);
    }

    public static class MissionNotFoundException extends RuntimeException {
        public MissionNotFoundException(String missionId, String groupId) {
            super("Missao nao encontrada: " + missionId + " / " + groupId);
        }
    }
}

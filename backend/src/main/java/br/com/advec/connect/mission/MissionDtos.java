package br.com.advec.connect.mission;

public final class MissionDtos {

    private MissionDtos() {}

    public record MissionCompletionDto(String missionId, String groupId, boolean completed) {
        static MissionCompletionDto from(MissionCompletion entity) {
            return new MissionCompletionDto(entity.getMissionId(), entity.getGroupId(), entity.isCompleted());
        }
    }

    public record UpdateMissionRequest(boolean completed) {}
}

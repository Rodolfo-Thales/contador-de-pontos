package br.com.advec.connect.scoreboard;

import java.time.Instant;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public final class ScoreboardDtos {

    private ScoreboardDtos() {}

    public record GroupDto(String id, String name, long points) {
        static GroupDto from(ScoreGroup group) {
            return new GroupDto(group.getId(), group.getName(), group.getPoints());
        }
    }

    public record EntryDto(Long id, String groupId, String groupName, int amount, Instant createdAt) {
        static EntryDto from(ScoreEntry entry) {
            return new EntryDto(entry.getId(), entry.getGroup().getId(),
                entry.getGroup().getName(), entry.getAmount(), entry.getCreatedAt());
        }
    }

    public record ScoreboardDto(List<GroupDto> groups, List<EntryDto> history) {}

    public record AddPointsRequest(
        @NotNull(message = "Informe a quantidade de pontos.")
        @Min(value = -10000, message = "Quantidade minima: -10000.")
        @Max(value = 10000, message = "Quantidade maxima: 10000.")
        Integer amount
    ) {}
}

package br.com.advec.connect.scoreboard;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.advec.connect.scoreboard.ScoreboardDtos.EntryDto;
import br.com.advec.connect.scoreboard.ScoreboardDtos.GroupDto;
import br.com.advec.connect.scoreboard.ScoreboardDtos.ScoreboardDto;

@Service
public class ScoreboardService {

    private final ScoreGroupRepository groupRepository;
    private final ScoreEntryRepository entryRepository;

    public ScoreboardService(ScoreGroupRepository groupRepository, ScoreEntryRepository entryRepository) {
        this.groupRepository = groupRepository;
        this.entryRepository = entryRepository;
    }

    @Transactional(readOnly = true)
    public ScoreboardDto getScoreboard() {
        List<GroupDto> groups = groupRepository.findAll(org.springframework.data.domain.Sort.by("id"))
            .stream().map(GroupDto::from).toList();
        List<EntryDto> history = entryRepository.findTop20ByOrderByCreatedAtDesc()
            .stream().map(EntryDto::from).toList();
        return new ScoreboardDto(groups, history);
    }

    @Transactional
    public GroupDto addPoints(String groupId, int amount) {
        ScoreGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new GroupNotFoundException(groupId));
        group.addPoints(amount);
        entryRepository.save(new ScoreEntry(group, amount));
        return GroupDto.from(group);
    }

    @Transactional
    public void resetAll() {
        entryRepository.deleteAllInBatch();
        groupRepository.findAll().forEach(ScoreGroup::resetPoints);
    }

    public static class GroupNotFoundException extends RuntimeException {
        public GroupNotFoundException(String groupId) {
            super("Grupo nao encontrado: " + groupId);
        }
    }
}

package br.com.advec.connect.mission;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.advec.connect.mission.MissionDtos.MissionCompletionDto;
import br.com.advec.connect.mission.MissionDtos.UpdateMissionRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/missions")
public class MissionController {

    private final MissionService service;

    public MissionController(MissionService service) {
        this.service = service;
    }

    /** Publico: qualquer visitante ve o progresso das missoes. */
    @GetMapping
    public List<MissionCompletionDto> getAll() {
        return service.getAll();
    }

    /** Somente admin autenticado (garantido pelo SecurityConfig). */
    @PostMapping("/{missionId}/{groupId}")
    public MissionCompletionDto setCompletion(
            @PathVariable String missionId,
            @PathVariable String groupId,
            @Valid @RequestBody UpdateMissionRequest body) {
        return service.setCompletion(missionId, groupId, body.completed());
    }
}

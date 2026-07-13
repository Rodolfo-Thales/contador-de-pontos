package br.com.advec.connect.scoreboard;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.advec.connect.scoreboard.ScoreboardDtos.AddPointsRequest;
import br.com.advec.connect.scoreboard.ScoreboardDtos.GroupDto;
import br.com.advec.connect.scoreboard.ScoreboardDtos.ScoreboardDto;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ScoreboardController {

    private final ScoreboardService service;

    public ScoreboardController(ScoreboardService service) {
        this.service = service;
    }

    /** Publico: qualquer visitante ve o placar. */
    @GetMapping("/scoreboard")
    public ScoreboardDto getScoreboard() {
        return service.getScoreboard();
    }

    /** Somente admin autenticado (garantido pelo SecurityConfig). */
    @PostMapping("/groups/{groupId}/points")
    public GroupDto addPoints(@PathVariable String groupId, @Valid @RequestBody AddPointsRequest body) {
        return service.addPoints(groupId, body.amount());
    }

    /** Somente admin autenticado. */
    @PostMapping("/scoreboard/reset")
    public ResponseEntity<Void> reset() {
        service.resetAll();
        return ResponseEntity.noContent().build();
    }
}

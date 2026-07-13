package br.com.advec.connect.scoreboard;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreEntryRepository extends JpaRepository<ScoreEntry, Long> {
    List<ScoreEntry> findTop20ByOrderByCreatedAtDesc();
}

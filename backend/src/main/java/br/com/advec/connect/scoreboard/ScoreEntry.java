package br.com.advec.connect.scoreboard;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_entries")
public class ScoreEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "group_id")
    private ScoreGroup group;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private Instant createdAt;

    protected ScoreEntry() {}

    public ScoreEntry(ScoreGroup group, int amount) {
        this.group = group;
        this.amount = amount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public ScoreGroup getGroup() { return group; }
    public int getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
}

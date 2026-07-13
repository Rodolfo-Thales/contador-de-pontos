package br.com.advec.connect.scoreboard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "score_groups")
public class ScoreGroup {

    @Id
    @Column(length = 40)
    private String id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(nullable = false)
    private long points;

    protected ScoreGroup() {}

    public ScoreGroup(String id, String name) {
        this.id = id;
        this.name = name;
        this.points = 0;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public long getPoints() { return points; }

    public void addPoints(int amount) {
        this.points = Math.max(0, this.points + amount);
    }

    public void resetPoints() {
        this.points = 0;
    }
}

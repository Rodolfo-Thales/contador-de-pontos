package br.com.advec.connect.mission;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "mission_completions", uniqueConstraints = @UniqueConstraint(columnNames = { "mission_id", "group_id" }))
public class MissionCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mission_id", nullable = false, length = 40)
    private String missionId;

    @Column(name = "group_id", nullable = false, length = 40)
    private String groupId;

    @Column(nullable = false)
    private boolean completed;

    protected MissionCompletion() {}

    public MissionCompletion(String missionId, String groupId, boolean completed) {
        this.missionId = missionId;
        this.groupId = groupId;
        this.completed = completed;
    }

    public Long getId() { return id; }
    public String getMissionId() { return missionId; }
    public String getGroupId() { return groupId; }
    public boolean isCompleted() { return completed; }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}

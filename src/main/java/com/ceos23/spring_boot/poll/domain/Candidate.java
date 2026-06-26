package com.ceos23.spring_boot.poll.domain;

import com.ceos23.spring_boot.global.domain.BaseEntity;
import com.ceos23.spring_boot.global.type.Part;
import com.ceos23.spring_boot.global.type.Team;
import jakarta.persistence.*;

@Entity
public class Candidate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private Part part;

    @Enumerated(EnumType.STRING)
    private Team team;

    @Column(nullable = false)
    private int voteCount;

    protected Candidate() {
    }

    private Candidate(Poll poll, String name, Part part, Team team) {
        this.poll = poll;
        this.name = name;
        this.part = part;
        this.team = team;
        this.voteCount = 0;
    }

    public static Candidate of(Poll poll, String name, Part part, Team team) {
        return new Candidate(poll, name, part, team);
    }

    public void increaseVoteCount() {
        this.voteCount++;
    }

    public void decreaseVoteCount() {
        if (this.voteCount > 0) {
            this.voteCount--;
        }
    }

    public boolean belongsTo(Poll poll) {
        return this.poll.getId().equals(poll.getId());
    }

    public Long getId() {
        return id;
    }

    public Poll getPoll() {
        return poll;
    }

    public String getName() {
        return name;
    }

    public Part getPart() {
        return part;
    }

    public Team getTeam() {
        return team;
    }

    public int getVoteCount() {
        return voteCount;
    }
}
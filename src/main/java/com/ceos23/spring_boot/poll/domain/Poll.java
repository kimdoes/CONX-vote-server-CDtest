package com.ceos23.spring_boot.poll.domain;

import com.ceos23.spring_boot.global.domain.BaseEntity;
import com.ceos23.spring_boot.global.type.VoteType;
import jakarta.persistence.*;

@Entity
public class Poll extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VoteType voteType;

    @Column(nullable = false)
    private boolean active;

    protected Poll() {
    }

    private Poll(String title, VoteType voteType) {
        this.title = title;
        this.voteType = voteType;
        this.active = true;
    }

    public static Poll of(String title, VoteType voteType) {
        return new Poll(title, voteType);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public boolean isActive() {
        return active;
    }
}
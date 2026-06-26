package com.ceos23.spring_boot.vote.domain;

import com.ceos23.spring_boot.global.domain.BaseEntity;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.user.domain.Member;
import jakarta.persistence.*;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vote_member_poll",
                        columnNames = {"member_id", "poll_id"}
                )
        }
)
public class Vote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poll_id", nullable = false)
    private Poll poll;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    protected Vote() {
    }

    private Vote(Member member, Poll poll, Candidate candidate) {
        this.member = member;
        this.poll = poll;
        this.candidate = candidate;
    }

    public static Vote of(Member member, Poll poll, Candidate candidate) {
        return new Vote(member, poll, candidate);
    }

    public void changeCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Poll getPoll() {
        return poll;
    }

    public Candidate getCandidate() {
        return candidate;
    }
}
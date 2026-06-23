package com.ceos23.spring_boot.user.domain;

import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.global.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    protected Member(
            String userLogInId, String password, String userEmail, Part part, String username, Team team
    ){
        this.userLogInId = userLogInId;
        this.password = password;
        this.userEmail = userEmail;
        this.part = part;
        this.username = username;
        this.team = team;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String userLogInId;

    private String password;

    private String userEmail;

    private Part part;

    private String username;

    private Team team;

    public static Member create(String userId, String password, String userEmail, Part part, String username, Team team){
        return new Member(userId, password, userEmail, part, username, team);
    }

    public static Member create(SignupRequest req, String encodedPassword){
        return new Member(
                req.userId(),
                encodedPassword,
                req.email(),
                req.part(),
                req.username(),
                req.team()
        );
    }
}

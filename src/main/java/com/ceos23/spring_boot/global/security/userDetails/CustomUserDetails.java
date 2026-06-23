package com.ceos23.spring_boot.global.security.userDetails;

import com.ceos23.spring_boot.user.domain.Member;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {
    private CustomUserDetails(long id, String userLoginId, String password,
                              Collection<? extends GrantedAuthority> role){
        super();
        this.id = id;
        this.userLoginId = userLoginId;
        this.password = password;
        this.role = role;
    }

    @Getter
    private long id;

    @Getter
    private String userLoginId;

    private String password;

    private Collection<? extends GrantedAuthority> role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role;
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userLoginId;
    }

    public static CustomUserDetails of(Member member){
        Collection<? extends GrantedAuthority> role = List.of(new SimpleGrantedAuthority("MEMBER"));

        return new CustomUserDetails(member.getId(), member.getUserLogInId(), member.getPassword(), role);
    }
}

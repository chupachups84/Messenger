package com.chernyshev.messenger.models;

import com.chernyshev.messenger.models.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",indexes = {
        @Index(columnList = "username"),
        @Index(columnList = "email"),
        @Index(columnList = "emailToken")
})

public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String lastname;

    private String firstname;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String username;

    private String password;

    private String emailToken;

    private String bio;

    private String status;

    private String avatarUrl;

    @Builder.Default
    private boolean isReceiptMessagesFriendOnly = false;

    @Builder.Default
    private boolean isFriendsListHidden = false;

    @Builder.Default
    @Getter(AccessLevel.NONE)
    private boolean isActive = true;

    @Transient
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}

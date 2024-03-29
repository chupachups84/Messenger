package com.chernyshev.messenger.services;

import com.chernyshev.messenger.exceptions.custom.InvalidUsernameOrPasswordException;
import com.chernyshev.messenger.models.UserEntity;
import com.chernyshev.messenger.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user =userRepository.findByUsername(username).filter(UserEntity::isEnabled)
                .orElseThrow(()-> new InvalidUsernameOrPasswordException(UserService.NOT_FOUND_MESSAGE));
        return new User(
                user.getUsername(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

}


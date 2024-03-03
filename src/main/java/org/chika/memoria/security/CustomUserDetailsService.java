package org.chika.memoria.security;

import lombok.RequiredArgsConstructor;
import org.chika.memoria.models.User;
import org.chika.memoria.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(final String id) {
        final User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        return UserPrincipal.create(user);
    }
}
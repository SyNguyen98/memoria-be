package org.chika.memoria.services;

import lombok.AllArgsConstructor;
import org.chika.memoria.exceptions.ResourceNotFoundException;
import org.chika.memoria.models.User;
import org.chika.memoria.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(final String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}

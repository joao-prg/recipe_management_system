package com.joaogoncalves.recipes.service;

import com.joaogoncalves.recipes.entity.User;
import com.joaogoncalves.recipes.exception.UserAlreadyExistsException;
import com.joaogoncalves.recipes.model.UserCreate;
import com.joaogoncalves.recipes.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void create(final UserCreate userCreate) {
        if (userRepository.existsByEmail(userCreate.getEmail())) {
            throw new UserAlreadyExistsException(
                    String.format("User already exists! [e-mail: %s]", userCreate.getEmail())
            );
        }
        User user = modelMapper.map(userCreate, User.class);
        user.setPassword(passwordEncoder.encode(userCreate.getPassword()));
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User not found! [e-mail: %s]", email))
                );
    }
}


package com.joaogoncalves.recipes.repository;

import com.joaogoncalves.recipes.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<UserDetails> findByEmail(String email);

    boolean existsByEmail(String email);
}

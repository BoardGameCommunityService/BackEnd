// src/main/java/com/boardmate/repository/UserRepository.java
package com.boardmate.repository;

import com.boardmate.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByProviderAndSocialId(String provider, String socialId);
}

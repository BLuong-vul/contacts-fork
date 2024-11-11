package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findById(long id);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.bio = :bio WHERE u.id = :id")
    void updateBioById(Long id, String bio);
}

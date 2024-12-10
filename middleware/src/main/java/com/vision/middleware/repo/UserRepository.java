package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Date;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> findByUsername(String username);
    Optional<ApplicationUser> findById(long id);
    List<ApplicationUser> findAllByUsernameContainingIgnoreCase(String query);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.profilePictureFileName = :profilePictureFileName WHERE u.id = :id")
    void updateProfilePictureFileNameById(Long id, String profilePictureFileName);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.bannerPictureFileName = :bannerPictureFileName WHERE u.id = :id")
    void updateBannerPictureFileNameById(Long id, String bannerPictureFileName);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.displayName = :displayName WHERE u.id = :id")
    void updateDisplayNameById(Long id, String displayName);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.bio = :bio WHERE u.id = :id")
    void updateBioById(Long id, String bio);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.occupation = :occupation WHERE u.id = :id")
    void updateOccupationById(Long id, String occupation);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.location = :location WHERE u.id = :id")
    void updateLocationById(Long id, String location);

    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.birthdate = :birthdate WHERE u.id = :id")
    void updateBirthdateById(Long id, Date birthdate);
}

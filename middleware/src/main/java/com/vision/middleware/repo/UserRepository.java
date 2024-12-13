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

/**
 * Repository for managing ApplicationUser entities, providing basic CRUD operations
 * and additional custom query methods for specific use cases.
 */
@Repository
public interface UserRepository extends JpaRepository<ApplicationUser, Long> {

    /**
     * Finds an ApplicationUser by username.
     *
     * @param username the username to search for
     * @return an Optional containing the ApplicationUser if found, or empty if not found
     */
    Optional<ApplicationUser> findByUsername(String username);

    /**
     * Finds an ApplicationUser by ID.
     *
     * @param id the ID of the ApplicationUser to search for
     * @return an Optional containing the ApplicationUser if found, or empty if not found
     */
    Optional<ApplicationUser> findById(long id);

    /**
     * Finds all ApplicationUsers whose usernames contain the specified query (case-insensitive).
     *
     * @param query the substring to search for in usernames
     * @return a List of ApplicationUsers whose usernames contain the specified query
     */
    List<ApplicationUser> findAllByUsernameContainingIgnoreCase(String query);

    /**
     * Updates the profile picture file name for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param profilePictureFileName the new profile picture file name
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.profilePictureFileName = :profilePictureFileName WHERE u.id = :id")
    void updateProfilePictureFileNameById(Long id, String profilePictureFileName);

    /**
     * Updates the banner picture file name for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param bannerPictureFileName the new banner picture file name
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.bannerPictureFileName = :bannerPictureFileName WHERE u.id = :id")
    void updateBannerPictureFileNameById(Long id, String bannerPictureFileName);

    /**
     * Updates the display name for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param displayName the new display name
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.displayName = :displayName WHERE u.id = :id")
    void updateDisplayNameById(Long id, String displayName);

    /**
     * Updates the bio for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param bio the new bio
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.bio = :bio WHERE u.id = :id")
    void updateBioById(Long id, String bio);

    /**
     * Updates the occupation for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param occupation the new occupation
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.occupation = :occupation WHERE u.id = :id")
    void updateOccupationById(Long id, String occupation);

    /**
     * Updates the location for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param location the new location
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.location = :location WHERE u.id = :id")
    void updateLocationById(Long id, String location);

    /**
     * Updates the birthdate for the ApplicationUser with the specified ID.
     *
     * @param id the ID of the ApplicationUser to update
     * @param birthdate the new birthdate
     */
    @Modifying
    @Transactional
    @Query("UPDATE ApplicationUser u SET u.birthdate = :birthdate WHERE u.id = :id")
    void updateBirthdateById(Long id, Date birthdate);
}

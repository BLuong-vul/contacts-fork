package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing database operations related to Notifications.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Retrieves a Notification by its unique identifier.
     *
     * @param id the unique identifier of the Notification to be retrieved
     * @return an Optional containing the Notification if found, otherwise an empty Optional
     */
    Optional<Notification> findById(long id);

    /**
     * Retrieves a list of Notifications associated with a specific ApplicationUser.
     *
     * @param user the ApplicationUser for whom the associated Notifications are to be retrieved
     * @return a list of Notifications associated with the given ApplicationUser
     */
    List<Notification> findByAssociatedUser(ApplicationUser user);

    /**
     * Retrieves a list of unacknowledged Notifications associated with a specific ApplicationUser.
     *
     * @param user the ApplicationUser for whom the unacknowledged associated Notifications are to be retrieved
     * @return a list of unacknowledged Notifications (where acknowledged = false) associated with the given ApplicationUser
     */
    List<Notification> findByAssociatedUserAndAcknowledgedFalse(ApplicationUser user);
}

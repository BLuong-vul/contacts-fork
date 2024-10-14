package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findById(long id);
    List<Notification> findByAssociatedUser(ApplicationUser user);
    List<Notification> findByAssociatedUserAndAcknowledgedFalse(ApplicationUser user);
}

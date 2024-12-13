package com.vision.middleware.repo;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.domain.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for managing Message entities, providing basic CRUD operations and custom query methods.
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Retrieves a list of Messages based on the specified sender and receiver.
     *
     * @param sender    the ApplicationUser who sent the Messages
     * @param receiver  the ApplicationUser who received the Messages
     * @return a List of Messages matching the sender and receiver criteria
     */
    List<Message> findBySendingUserIdAndReceivingUserId(ApplicationUser sender, ApplicationUser receiver);
}

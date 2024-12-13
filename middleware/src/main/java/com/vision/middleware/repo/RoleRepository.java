package com.vision.middleware.repo;

import com.vision.middleware.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data access object for managing {@link Role} entities.
 * Provides basic CRUD operations and a custom finder by authority.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Retrieves a {@link Role} by its authority, if present.
     *
     * @param authority the authority to search for
     * @return an {@link Optional} containing the {@link Role} if found, otherwise an empty {@link Optional}
     */
    Optional<Role> findByAuthority(String authority);
}

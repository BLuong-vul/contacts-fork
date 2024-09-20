package com.vision.middleware.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.vision.middleware.domain.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
	Optional<User> findByUserId(int userId);
}
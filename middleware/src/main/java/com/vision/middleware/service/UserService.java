package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Date;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ApplicationUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("user " + username + " not found")
        );
    }

    public ApplicationUser loadUserById(long id) throws IdNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("id " + id + " not found")
        );
    }

    @Transactional
    public int getFollowerCount(ApplicationUser user){
        // System.out.println("DEBUG: " + user.getFollowers().size());
        return user.getFollowers().size();
    }

    @Transactional
    public int getFollowingCount(ApplicationUser user){
        return user.getFollowing().size();
    }

    // Editing profile
    public void updateDisplayNameById(Long id, String displayName){
        userRepository.updateDisplayNameById(id, displayName);
    }

    public void updateBioById(Long id, String bio){
        userRepository.updateBioById(id, bio);
    }

    public void updateOccupationById(Long id, String occupation){
        userRepository.updateOccupationById(id, occupation);
    }

    public void updateLocationById(Long id, String location){
        userRepository.updateLocationById(id, location);
    }

    public void updateBirthdateById(long id, Date birthdate){
        userRepository.updateBirthdateById(id, birthdate);
    }

    public List<ApplicationUser> searchUsers(String query) {
        return userRepository.findAllByUsernameContainingIgnoreCase(query);
    }

    public Collection<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}
package com.vision.middleware.service;

import com.vision.middleware.domain.ApplicationUser;
import com.vision.middleware.exceptions.IdNotFoundException;
import com.vision.middleware.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Service class responsible for managing user-related operations.
 * Implemented as part of the UserDetailsService to load users by username.
 * Provides additional methods for user profile management and queries.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    /**
     * Repository instance for user data access.
     */
    @Autowired
    private final UserRepository userRepository;

    /**
     * Loads a user by their username, throwing an exception if not found.
     *
     * @param username the username to search for
     * @return the ApplicationUser instance associated with the username
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public ApplicationUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("user " + username + " not found")
        );
    }

    /**
     * Retrieves a user by their ID, throwing an exception if not found.
     *
     * @param id the ID of the user to retrieve
     * @return the ApplicationUser instance associated with the ID
     * @throws IdNotFoundException if the user is not found
     */
    public ApplicationUser loadUserById(long id) throws IdNotFoundException {
        return userRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("id " + id + " not found")
        );
    }

    /**
     * Returns the number of followers for the given user.
     *
     * @param user the user to query followers for
     * @return the number of followers
     */
    @Transactional
    public int getFollowerCount(ApplicationUser user){
        return user.getFollowers().size();
    }

    /**
     * Returns the number of users the given user is following.
     *
     * @param user the user to query following for
     * @return the number of users user is following
     */
    @Transactional
    public int getFollowingCount(ApplicationUser user){
        return user.getFollowing().size();
    }

    /**
     * Updates the profile picture file name for a user by their ID.
     *
     * @param id                  the ID of the user to update
     * @param profilePictureFileName the new profile picture file name
     */
    public void updateProfilePictureById(long id, String profilePictureFileName) {
        userRepository.updateProfilePictureFileNameById(id, profilePictureFileName);
    }

    /**
     * Updates the banner picture file name for a user by their ID.
     *
     * @param id                  the ID of the user to update
     * @param bannerPictureFileName the new banner picture file name
     */
    public void updateBannerPictureById(long id, String bannerPictureFileName) {
        userRepository.updateBannerPictureFileNameById(id, bannerPictureFileName);
    }

    /**
     * Updates the display name for a user by their ID.
     *
     * @param id        the ID of the user to update
     * @param displayName the new display name
     */
    public void updateDisplayNameById(Long id, String displayName){
        userRepository.updateDisplayNameById(id, displayName);
    }

    /**
     * Updates the bio for a user by their ID.
     *
     * @param id  the ID of the user to update
     * @param bio the new bio
     */
    public void updateBioById(Long id, String bio){
        userRepository.updateBioById(id, bio);
    }

    /**
     * Updates the occupation for a user by their ID.
     *
     * @param id        the ID of the user to update
     * @param occupation the new occupation
     */
    public void updateOccupationById(Long id, String occupation){
        userRepository.updateOccupationById(id, occupation);
    }

    /**
     * Updates the location for a user by their ID.
     *
     * @param id       the ID of the user to update
     * @param location the new location
     */
    public void updateLocationById(Long id, String location){
        userRepository.updateLocationById(id, location);
    }

    /**
     * Updates the birthdate for a user by their ID.
     *
     * @param id        the ID of the user to update
     * @param birthdate the new birthdate
     */
    public void updateBirthdateById(long id, Date birthdate){
        userRepository.updateBirthdateById(id, birthdate);
    }

    /**
     * Searches for users by a query string (username contains ignore case).
     *
     * @param query the search query string
     * @return a list of ApplicationUser instances matching the query
     */
    public List<ApplicationUser> searchUsers(String query) {
        return userRepository.findAllByUsernameContainingIgnoreCase(query);
    }

    /**
     * Returns a collection of GrantedAuthority instances for the given role.
     * Currently, returns a single SimpleGrantedAuthority for the provided role.
     *
     * @param role the role to generate authorities for
     * @return a collection of GrantedAuthority instances
     */
    public Collection<GrantedAuthority> getAuthorities(String role) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }
}

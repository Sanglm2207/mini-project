package com.kaidev99.training.miniproject.service;

import com.kaidev99.training.miniproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Provides a UserDetailsService bean for Spring Security.
     * This bean is responsible for loading a user by their username and returning a UserDetails object.
     *
     * @return An implementation of UserDetailsService.
     */
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            /**
             * Locates the user based on the username.
             *
             * @param username the username identifying the user whose data is required.
             * @return a fully populated user record (never {@code null}).
             * @throws UsernameNotFoundException if the user could not be found or the user has no
             *                                   GrantedAuthority.
             */
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
            }
        };
    }
}

package in.srinithamizh.oxygen_cylinder.service;

import in.srinithamizh.oxygen_cylinder.entity.Role;
import in.srinithamizh.oxygen_cylinder.entity.User;
import in.srinithamizh.oxygen_cylinder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final int CREDENTIALS_EXPIRATION_DURATION_IN_DAYS = 90;
    private static final int ACCOUNT_UNLOCK_DURATION_IN_DAYS = 1;
    private static final int RESET_FAILED_LOGIN_ATTEMPTS = 0;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registration(User user, Role role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setRoles(Set.of(role));
        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findUserByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} is not found", username);
                    return new UsernameNotFoundException(String.format("username '%s' is not found!", username));
                });

        if (!user.isAccountNonLocked() && isAccountLockedExpired(user)) {
            logger.info("User Account unlocked for username: {}", username);
            return unlockAccount(user);
        }

        if (isPasswordExpired(user.getPasswordChangedAt())) {
            logger.info("User account credentials are expired for username: {}", username);
            user.setCredentialsNonExpired(false);
            return user;
        }
        return user;
    }

    public void failedLogin(String username) {
        User user = findUserByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} is not found", username);
                    return new UsernameNotFoundException(String.format("username '%s' is not found!", username));
                });

        int newFailureAttempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(newFailureAttempts);

        logger.warn("Failed login attempts {} for username: {}", newFailureAttempts, username);

        if (newFailureAttempts >= 3) {
            logger.warn("User account locked for username: {}", username);
            user.setAccountNonLocked(false);
            user.setAccountLockedAt(LocalDateTime.now());
        }
        userRepository.save(user);
    }



    private boolean isAccountLockedExpired(User user) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime unlockDate = user.getAccountLockedAt().plusDays(ACCOUNT_UNLOCK_DURATION_IN_DAYS);
        return currentDate.isAfter(unlockDate);
    }

    private User unlockAccount(User user) {
        user.setAccountNonLocked(true);
        user.setFailedLoginAttempts(RESET_FAILED_LOGIN_ATTEMPTS);
        return userRepository.save(user);
    }

    private boolean isPasswordExpired(LocalDateTime passwordChangedAt) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime passwordExpirationDate = passwordChangedAt.plusDays(CREDENTIALS_EXPIRATION_DURATION_IN_DAYS);
        return currentDate.isAfter(passwordExpirationDate);
    }
}

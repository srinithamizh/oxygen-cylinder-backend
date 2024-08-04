package in.srinithamizh.oxygen_cylinder.service;

import in.srinithamizh.oxygen_cylinder.entity.Role;
import in.srinithamizh.oxygen_cylinder.entity.User;
import in.srinithamizh.oxygen_cylinder.repository.UserRepository;
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
        Optional<User> user = findUserByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("username '%s' is not found!", username));
        }

        if (!user.get().isAccountNonLocked() && isAccountLockedExpired(user.get())) {
            return unlockAccount(user.get());
        }

        if (isPasswordExpired(user.get().getPasswordChangedAt())) {
            user.get().setCredentialsNonExpired(false);
            return user.get();
        }

        return user.get();
    }

    public void failedLogin(String username) {
        Optional<User> user = findUserByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(String.format("username '%s' is not found!", username));
        }

        int newFailureAttempts = user.get().getFailedLoginAttempts() + 1;
        user.get().setFailedLoginAttempts(newFailureAttempts);
        if (newFailureAttempts >= 3) {
            user.get().setAccountNonLocked(false);
            user.get().setAccountLockedAt(LocalDateTime.now());
        }
        userRepository.save(user.get());
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

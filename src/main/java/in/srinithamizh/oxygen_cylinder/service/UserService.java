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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int EXPIRATION_DAYS = 90;

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
            throw new UsernameNotFoundException(username);
        }

        if (isPasswordExpired(user.get().getPasswordChangedAt())) {
            user.get().setCredentialsNonExpired(false);
            return user.get();
        }

        return user.get();
    }

    private boolean isPasswordExpired(LocalDateTime passwordChangedAt) {
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime passwordExpirationDate = passwordChangedAt.plusDays(EXPIRATION_DAYS);
        return currentDate.isAfter(passwordExpirationDate);
    }
}

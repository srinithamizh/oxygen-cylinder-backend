package in.srinithamizh.oxygen_cylinder.service;

import in.srinithamizh.oxygen_cylinder.entity.User;
import in.srinithamizh.oxygen_cylinder.exception.RedundantPasswordException;
import in.srinithamizh.oxygen_cylinder.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ResetPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean resetPasswordByUsername(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Username {} is not found", username);
                    return new UsernameNotFoundException(String.format("username '%s' is not found!", username));
                });

        logger.info("Password reset requested for username: {}", username);

        if(passwordEncoder.matches(password, user.getPassword())) {
            logger.info("New Password is same as the current password for username: {}", username);
            throw new RedundantPasswordException("The new password cannot be the same as the current password.");
        }

        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return true;
    }
}

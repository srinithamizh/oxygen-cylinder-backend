package in.srinithamizh.oxygen_cylinder.controller;

import in.srinithamizh.oxygen_cylinder.dto.ResetPassword;
import in.srinithamizh.oxygen_cylinder.service.ResetPasswordService;
import in.srinithamizh.oxygen_cylinder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ResetPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);
    private final ResetPasswordService resetPasswordService;
    public ResetPasswordController(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPassword resetPassword) {
        boolean updated  = resetPasswordService
                .resetPasswordByUsername(resetPassword.getUsername(), resetPassword.getPassword());
        if (updated) {
            logger.info("Password reset successfully for username: {}", resetPassword.getUsername());
            return ResponseEntity.ok("Password updated successfully");
        } else {
            logger.error("Password reset failed for username: {}, User not found", resetPassword.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}

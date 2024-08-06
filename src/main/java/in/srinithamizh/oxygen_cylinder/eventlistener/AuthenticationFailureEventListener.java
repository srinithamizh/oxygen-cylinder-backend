package in.srinithamizh.oxygen_cylinder.eventlistener;

import in.srinithamizh.oxygen_cylinder.service.UserService;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureEventListener
        implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final UserService userService;

    public AuthenticationFailureEventListener(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String username = (String) event.getAuthentication().getPrincipal();
        userService.failedLogin(username);
    }
}

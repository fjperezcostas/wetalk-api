package fjpc.wetalk.security.auth;

import fjpc.wetalk.security.jwt.JwtToken;
import fjpc.wetalk.security.jwt.JwtUtils;
import fjpc.wetalk.security.users.ApplicationUser;
import fjpc.wetalk.security.users.ApplicationUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    private JwtUtils jwt;

    private ApplicationUserRepository userRepository;

    public AuthController(AuthService authService, JwtUtils jwt, ApplicationUserRepository userRepository) {
        this.authService = authService;
        this.jwt = jwt;
        this.userRepository = userRepository;
    }

    @PostMapping("/login")
    public JwtToken login(@RequestBody Credentials credentials) {
        Authentication authentication = authService.authenticate(credentials);
        return jwt.generateToken(authentication);
    }

    @GetMapping("/me")
    public ApplicationUser whoAmI(Principal principal) {
        return userRepository.findByUsername(principal.getName());
    }

}

package fjpc.wetalk.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private AuthenticationManager auth;

    public AuthService(AuthenticationManager auth) {
        this.auth = auth;
    }

    public Authentication authenticate(Credentials credentials) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();
        Authentication authentication = auth.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }

}

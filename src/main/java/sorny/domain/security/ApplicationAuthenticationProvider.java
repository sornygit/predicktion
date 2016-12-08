package sorny.domain.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sorny.domain.user.UserEntity;
import sorny.domain.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationAuthenticationProvider implements AuthenticationProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationAuthenticationProvider.class);

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        try {
            UserEntity user = userService.getByUsername(username);

            String inputPassword = String.valueOf(authentication.getCredentials());
            if (!user.isPassword(inputPassword)) // todo: keep track of failed password attempts per user and lock them out if > limit?
                throw new BadCredentialsException("Wrong password supplied.");

            List<GrantedAuthority> authorities = new ArrayList<>();

            if (username.equals("admin"))
                authorities.add(new SimpleGrantedAuthority("admin"));
            authorities.add(new SimpleGrantedAuthority("user"));

            LOGGER.info(username + " logged in at " + LocalDateTime.now());

            return new UsernamePasswordAuthenticationToken(user.getUsername(), user.password, authorities);
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Username not found: " + username);
        } catch (Throwable e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass));
    }
}

package ru.er_log.stock.auth.configs.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.er_log.stock.auth.services.UserDetailsImpl;

import java.util.Date;

@Component
public class JwtUtils {

    private final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);

    @Autowired
    private Environment environment;

    private String getJwtSecret() {
        String v = environment.getProperty("JWT_SECRET");
        if (v == null) {
            throw new IllegalStateException("JWT_SECRET not presented");
        }
        return v;
    }

    private Long getJwtExpirationMs() {
        String v = environment.getProperty("JWT_EXPIRATION_MS");
        if (v == null) {
            throw new IllegalStateException("JWT_EXPIRATION_MS not presented");
        }
        return Long.valueOf(v);
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        Date date = new Date();
        return Jwts.builder().setSubject((userPrincipal.getUsername())).setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + getJwtExpirationMs()))
                .signWith(SignatureAlgorithm.HS512, getJwtSecret()).compact();
    }

    public boolean validateJwtToken(String jwt) {
        try {
            Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException | IllegalArgumentException e) {
            LOG.error(e.getMessage());
        }

        return false;
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parser().setSigningKey(getJwtSecret()).parseClaimsJws(jwt).getBody().getSubject();
    }
}

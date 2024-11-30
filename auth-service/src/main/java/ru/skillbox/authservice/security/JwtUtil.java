package ru.skillbox.authservice.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skillbox.authservice.domain.User;

import java.util.Date;

import static ru.skillbox.authservice.security.SecurityConstants.TOKEN_PREFIX;

@Component
public class JwtUtil {

    @Value("${jwt.expiration-time}")
    private Long expirationTime;

    @Autowired
    public Algorithm algorithm;

    public Date makeExpirationDate() {
        return new Date(System.currentTimeMillis() + expirationTime);
    }

    public String generateToken(User user) {
        return JWT.create()
                .withIssuer("http://skillbox.ru")
                .withIssuedAt(new Date())
                .withExpiresAt(makeExpirationDate())
                .withSubject(user.getName())
                .withClaim("id", user.getId())
                .withExpiresAt(makeExpirationDate())
                .sign(algorithm);
    }

    public String getSubjectFromToken(String token) {
        return JWT.require(algorithm)
                .build()
                .verify(token.replace(TOKEN_PREFIX, ""))
                .getSubject();
    }
}

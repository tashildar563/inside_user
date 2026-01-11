package com.user.inside_user.security.jwtAuth;


import com.user.inside_user.security.user.UserDetail;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
/**
 * The JwtUtils class provides utility methods for JWT operations.
 * It includes methods for generating a JWT for a user, validating a JWT, and getting the username from a JWT.
 * The class uses the Jwts library for JWT operations.
 * It uses the @Value annotation to inject the JWT secret and expiration time from the application properties.
 * The class also includes a private method for generating a signing key based on the JWT secret.
 * It uses the LoggerFactory for logging.
 */
@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    @Value("${auth.token.expirationInMils}")
    private int jwtExpirationMs;

    public JwtUtils() {
    }

    public String generateJwtTokenForUser(Authentication authentication) {
        UserDetail userPrincipal = (UserDetail)authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        return Jwts.builder().setSubject(userPrincipal.getUsername()).claim("roles", roles).setIssuedAt(new Date()).setExpiration(new Date((new Date()).getTime() + (long)this.jwtExpirationMs)).signWith(this.key(), SignatureAlgorithm.HS256).compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor((byte[])Decoders.BASE64.decode(this.jwtSecret));
    }

    public String getUserNameFromToken(String token) {
        return ((Claims)Jwts.parserBuilder().setSigningKey(this.key()).build().parseClaimsJws(token).getBody()).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.key()).build().parse(token);
            return true;
        } catch (MalformedJwtException var3) {
            MalformedJwtException e = var3;
            logger.error("Invalid jwt token : {} ", e.getMessage());
        } catch (ExpiredJwtException var4) {
            ExpiredJwtException e = var4;
            logger.error("Expired token : {} ", e.getMessage());
        } catch (UnsupportedJwtException var5) {
            UnsupportedJwtException e = var5;
            logger.error("This token is not supported : {} ", e.getMessage());
        } catch (IllegalArgumentException var6) {
            IllegalArgumentException e = var6;
            logger.error("No  claims found : {} ", e.getMessage());
        }

        return false;
    }
}


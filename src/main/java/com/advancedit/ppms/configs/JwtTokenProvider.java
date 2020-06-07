package com.advancedit.ppms.configs;

import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import com.advancedit.ppms.exceptions.ErrorCode;
import com.advancedit.ppms.exceptions.PPMSException;
import com.advancedit.ppms.utils.LoggedUserInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.advancedit.ppms.models.user.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {

	@Value("${security.jwt.token.secret-key:secret}")
    private String secretKey = "secret";
	
    @Value("${security.jwt.token.expire-length:7200000}")
    private long validityInMilliseconds = 7200000; // 2h

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    /*public String createToken(String username, Set<Role> set) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", set);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
            .setClaims(claims)//
            .setIssuedAt(now)//
            .setExpiration(validity)//
            .signWith(SignatureAlgorithm.HS256, secretKey)//
            .compact();
    }*/

    public String createToken(String email, Set<Role> roles, String moduleId, long tenantId) {
        Claims claims = Jwts.claims().setSubject(email);
        if (tenantId > 0) claims = claims.setIssuer(Long.toString(tenantId));
        claims.put("roles", roles);
        claims.put("moduleId", moduleId);
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);
        return Jwts.builder()//
                .setClaims(claims)//
                .setIssuedAt(now)//
                .setExpiration(validity)//
                .signWith(SignatureAlgorithm.HS256, secretKey)//
                .compact();
    }
    

    
    public LoggedUserInfo getUserInfoFromToken(String token) {
        Claims body = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        List<String> roles = (List<String>)body.getOrDefault("roles", Collections.emptyList());
        String moduleId = (String)body.get("moduleId");
        LoggedUserInfo userInfo = new LoggedUserInfo();
        userInfo.setEmail(body.getSubject());
        userInfo.setModuleId(moduleId);
        Optional.ofNullable(body.getIssuer())
        .ifPresent(s -> userInfo.setTenantId(Long.parseLong(s)));
        userInfo.setRoles(roles.stream().map(Role::valueOf).collect(Collectors.toSet()));
        return userInfo;
    }
    
    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
    
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new PPMSException(ErrorCode.TOKEN_EXPIRED, "Expired or invalid JWT token");
            //throw new AuthenticationException( "Expired or invalid JWT token");
        }
    }
}

package com.example.config;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTTokenHelper {

	@Value("${jwt.auth.app}")
	private String appName;
	
	@Value("${jwt.auth.secret_key}")
	private String secretKey;
	
	@Value("${jwt.auth.expires_in}")
	private int expiresIn;
	
	private SignatureAlgorithm SIGNATURE_ALGORTITHM = SignatureAlgorithm.HS256;
	
	private Claims getAllClaimsFromToken(String token) {
		Claims claims;
		try {
			claims = Jwts.parser()
					.setSigningKey(secretKey)
					.parseClaimsJws(token)
					.getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}
	
	public String getUsernameFromToken(String token) {
		String userName;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			userName = claims.getSubject();
		} catch (Exception e) {
			userName = null;
		}
		return userName;
	}
	
	public String generateToken(String userName) throws InvalidKeySpecException, NoSuchAlgorithmException {
		
		return Jwts.builder()
				.setIssuer(appName)
				.setSubject(userName)
				.setIssuedAt(new Date())
				.setExpiration(generateExpirationDate())
				.signWith(SIGNATURE_ALGORTITHM, secretKey)
				.compact();
	}
	
	private Date generateExpirationDate() {
		return new Date(new Date().getTime() + expiresIn * 1000);
	}
	
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String userName = getUsernameFromToken(token);
		return (
				userName != null &&
				userName.equals(userDetails.getUsername()) &&
				!isTokenExpired(token)
				);		
	}
	
	public boolean isTokenExpired(String token) {
		Date expiredDate = getExpirationDate(token);
		return expiredDate.before(new Date());
	}
	
	private Date getExpirationDate(String token) {
		Date expireDate;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			expireDate = claims.getExpiration();
		} catch (Exception e) {
			expireDate = null;
		}
		return expireDate;
	}
	
	public Date getIssuedAtDateFromToken(String token) {
		Date issuedAt;
		try {
			final Claims claims = this.getAllClaimsFromToken(token);
			issuedAt = claims.getIssuedAt();
		} catch (Exception e) {
			issuedAt = null;
		}
		return issuedAt;
	}
	
	public String getToken(HttpServletRequest request) {
		String authHeader = getAuthHeaderFromHeader(request);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7);
		}
		
		return null;
	}
	
	public String getAuthHeaderFromHeader(HttpServletRequest request) {
		return request.getHeader("Authorization");
	}
	
}

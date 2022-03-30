package com.example.controllers;

import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.spec.InvalidKeySpecException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.config.JWTTokenHelper;
import com.example.entities.User;
import com.example.requests.AuthenticationRequest;
import com.example.responses.LoginResponse;
import com.example.responses.UserInfo;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	JWTTokenHelper jwtTokenHelper;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@PostMapping("/auth/login")
	public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest) throws InvalidKeySpecException, NoSuchAlgorithmException {
		
		final Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		User user = (User)authentication.getPrincipal();
		
		String jwtToken = jwtTokenHelper.generateToken(user.getUsername());
		
		LoginResponse loginResponse = new LoginResponse();
		loginResponse.setToken(jwtToken);
		
		return ResponseEntity.ok(loginResponse);
	}
	
	@GetMapping("/auth/userinfo")
	public ResponseEntity<?> getUserInfo(Principal user) {
		User user2 = (User) userDetailsService.loadUserByUsername(user.getName());
		UserInfo userInfo = new UserInfo();
		userInfo.setFirstName(user2.getFirstName());
		userInfo.setLastName(user2.getLastName());
		userInfo.setRoles(user2.getAuthorities().toArray());
		
		return ResponseEntity.ok(userInfo);
	}
	
	
	
	
}

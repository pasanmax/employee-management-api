package com.example.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.services.UserService;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
	
	private UserService userService;
	private JWTTokenHelper jwtTokenHelper;
	
	public JWTAuthenticationFilter(UserService userService, JWTTokenHelper jwtTokenHelper) {
		this.userService = userService;
		this.jwtTokenHelper = jwtTokenHelper;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String authToken = jwtTokenHelper.getToken(request);
		
		if (authToken != null) {
			String userName = jwtTokenHelper.getUsernameFromToken(authToken);
			
			if (userName != null) {
				UserDetails userDetails = userService.loadUserByUsername(userName);
				
				if (jwtTokenHelper.validateToken(authToken, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetails(request));
					
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		}
		filterChain.doFilter(request, response);
		
	}

}

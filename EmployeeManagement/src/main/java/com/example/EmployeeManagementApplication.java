package com.example;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.entities.Authority;
import com.example.entities.User;
import com.example.repositories.UserRepository;

@SpringBootApplication
public class EmployeeManagementApplication {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	public static void main(String[] args) {
		SpringApplication.run(EmployeeManagementApplication.class, args);
	}
	
	@PostConstruct
	protected void init() {
		List<Authority> authorityList = new ArrayList<>();
		
		authorityList.add(createAuthority("USER", "User role"));
		authorityList.add(createAuthority("ADMIN", "Admin role"));
		
		User user = new User();
		user.setUserName("pasan");
		user.setFirstName("Pasan");
		user.setLastName("Hettiarachchi");
		user.setPassword(passwordEncoder.encode("pasan123"));
		user.setEmail("pasan.anjana98@outlook.com");
		user.setEnabled(true);
		user.setAuthorities(authorityList);
		
		userRepository.save(user);
	}
	
	private Authority createAuthority(String roleCode, String roleDescription) {
		Authority authority = new Authority();
		authority.setRoleCode(roleCode);
		authority.setRoleDescription(roleDescription);
		return authority;
	}

}

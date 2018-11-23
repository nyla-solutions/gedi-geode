package gedi.solutions.geode.spring.security;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import gedi.solutions.geode.spring.security.data.UserProfileDetails;

@Service
public interface SpringSecurityUserService extends UserDetailsService
{
	public void registerUser(UserProfileDetails userDetails);
}

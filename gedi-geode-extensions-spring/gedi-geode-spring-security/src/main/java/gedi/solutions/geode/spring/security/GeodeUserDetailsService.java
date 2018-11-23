package gedi.solutions.geode.spring.security;

import org.apache.geode.cache.Region;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import gedi.solutions.geode.spring.security.data.UserProfileDetails;

public class GeodeUserDetailsService implements SpringSecurityUserService
{
	
	private Region<String, UserProfileDetails> userRegion;
	
	
	public GeodeUserDetailsService(Region<String, UserProfileDetails> region)
	{
		this.userRegion = region;
	}

	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		UserDetails user = userRegion.get(username);
		
		return user;
	}

	public void registerUser(UserProfileDetails userDetails)
	{
		this.userRegion.create(userDetails.getUsername(), userDetails);
	}

}

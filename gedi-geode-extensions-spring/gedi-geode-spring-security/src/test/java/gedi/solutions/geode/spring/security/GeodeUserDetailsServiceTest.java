package gedi.solutions.geode.spring.security;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import org.apache.geode.cache.Region;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import gedi.solutions.geode.spring.security.data.UserProfileDetails;

public class GeodeUserDetailsServiceTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void testLoadUserByUsername()
	{
		GrantedAuthority[] roles = {new SimpleGrantedAuthority("READ"), new SimpleGrantedAuthority("WRITE")};
		
		UserProfileDetails nyla = new UserProfileDetails("nyla", "nyla", Arrays.asList(roles));


		Region<String, UserProfileDetails> region = mock(Region.class);
		when(region.get("nyla")).thenReturn(nyla);
		
		GeodeUserDetailsService us = new GeodeUserDetailsService(region);
		
		us.registerUser(nyla);
		
		verify(region).create(anyString(), any(UserProfileDetails.class));
		
		assertNull(us.loadUserByUsername(null));
		
		UserDetails ggreen = us.loadUserByUsername("ggreen");
		assertNull(ggreen);
		
		UserDetails nylaOut = us.loadUserByUsername("nyla");
		assertNotNull(nylaOut);
		
		 assertTrue(nylaOut
		 		.getAuthorities()
		 		.stream().anyMatch(a -> "READ".equals(a.getAuthority())));
		 
		 
		 assertTrue(nylaOut
	 		.getAuthorities()
	 		.stream().anyMatch(a -> "WRITE".equals(a.getAuthority())));
		 
			
	}

}

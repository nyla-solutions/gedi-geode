package gedi.solutions.geode.spring.security.data;

import static org.junit.Assert.*;
import org.junit.Test;

import gedi.solutions.geode.spring.security.data.RegistrationDTO;
import gedi.solutions.geode.spring.security.data.UserProfileDetails;

public class RegistrationDTOTest
{

	@Test
	public void testToUserDetails()
	{
		RegistrationDTO dto = new RegistrationDTO();
		dto.setPassword("password".toCharArray());
		dto.setUserName("nyla");
		dto.setFirstName("FirstName");
		dto.setLastName("LastName");
		
		UserProfileDetails details = dto.toUserDetails("READ");
		assertNotNull(details);
		assertEquals(details.getUsername(),"nyla");
		assertEquals(details.getPassword(),"password");
		assertEquals(details.getFirstName(),"FirstName");
		assertEquals(details.getLastName(),"LastName");
		
		assertTrue(details
		.getAuthorities()
		.stream().anyMatch(a -> "READ".equals(a.getAuthority())));
		
	}

}

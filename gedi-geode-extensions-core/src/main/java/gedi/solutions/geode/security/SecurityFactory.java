package gedi.solutions.geode.security;

import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.SecurityManager;

/**
 * SecurityFactory is representable for the create authorization/authentication objects
 * @author Gregory Green
 *
 */
public class SecurityFactory
{
	private SecurityFactory()
	{
	}
	/**
	 * 
	 * @return new CryptionPropertyAuthInitialize()
	 */
	public static AuthInitialize createAuthInitialize()
	{
		//DO NOT USE spring
		return new CryptionPropertyAuthInitialize();
	}// ------------------------------------------------

	/**
	 * 
	 * @return new LDAPAuthenticator()
	 */
	public static SecurityManager createAuthenticator()
	{
		return new LDAPAuthenticator();
	}// ------------------------------------------------
}

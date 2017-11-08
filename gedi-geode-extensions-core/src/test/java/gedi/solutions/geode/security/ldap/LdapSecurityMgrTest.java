package gedi.solutions.geode.security.ldap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import javax.naming.NamingException;

import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;
import org.junit.Test;
import static org.mockito.Mockito.*;

import gedi.solutions.geode.security.exceptions.MissingSecurityProperty;
import gedi.solutions.geode.security.ldap.LDAPConnectionFactory;
import gedi.solutions.geode.security.ldap.LdapSecurityMgr;
import nyla.solutions.core.ds.LDAP;
import nyla.solutions.core.ds.security.LdapSecurityGroup;
import nyla.solutions.core.ds.security.LdapSecurityUser;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;

public class LdapSecurityMgrTest
{

	static
	{
		System.setProperty(Cryption.CRYPTION_KEY_PROP, "GEDI-GEODE");
		Config.reLoad();
	}
	
	private static LDAP ldap;
	private static LogWriter securityLogger;
	private static LDAPConnectionFactory ldapConnectionFactory ;
	private static Cache cache;
	
	public LdapSecurityMgr init()
	throws NamingException
	{
		LdapSecurityMgr mgr = new LdapSecurityMgr();
		
		ldap = mock(LDAP.class);
		
		securityLogger = mock(LogWriter.class);
		cache = mock(Cache.class);
		ldapConnectionFactory = mock(LDAPConnectionFactory.class);
		
		when(ldapConnectionFactory.connect(any(), any(), any()))
		.thenReturn(ldap).thenReturn(ldap).thenReturn(ldap);
		
		when(cache.getSecurityLogger()).thenReturn(securityLogger);
		
		mgr.setLdapConnectionFactory(ldapConnectionFactory);
		
		Properties props  = new Properties();
		props.setProperty("security-ldap-proxy-dn", "uid=admin");
		props.setProperty("security-ldap-proxy-password", "password");
		props.setProperty("security-ldap-server-url", "ldap://localhost:389");
		props.setProperty("security-ldap-group-attribute", "CN");
		props.setProperty("security-ldap-base-dn", "basedn");
		props.setProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP, "memberOf");
		
		props.setProperty("security-ldap-acl-user-admin", "ALL");
		props.setProperty("security-ldap-acl-user-guest", "NONE");
		props.setProperty("security-ldap-acl-user-readonly", "DATA:READ");
		props.setProperty("security-ldap-acl-group-readonly","DATA:READ");
		
		mgr.setup(props, cache);
		
		System.out.println("setup");
		
		return mgr;
	}
	/**
	 * Testing the setup of the Ldap and making an initial connection
	 * @throws Exception when an unknown error occurs
	 */
	@Test
	public synchronized  void testSetup()
	throws Exception
	{
		LdapSecurityMgr mgr = init();
		LogWriter securityLogger = mock(LogWriter.class);
		Cache cache = mock(Cache.class);
		LDAPConnectionFactory ldapConnectionFactory = mock(LDAPConnectionFactory.class);
		
		when(cache.getSecurityLogger()).thenReturn(securityLogger);
		Properties props = new Properties();
		
		mgr.setLdapConnectionFactory(ldapConnectionFactory);
		
		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
	
		props.setProperty("security-ldap-proxy-dn", "admin");

		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-proxy-password", "password");

		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-server", "ldap://localhost:389");
		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		
		props.setProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP, "memberOf");
		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		
		props.setProperty(LdapSecurityConstants.LDAP_SERVER_URL_PROP, "test");
		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP, "test");
		try
		{
			mgr.setup(props,cache);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		
		props.setProperty("security-ldap-base-dn", "password");
		mgr.setup(props,cache);
		

	}//------------------------------------------------
	@Test
	public void testSetupPassword() throws Exception
	{

		
		LdapSecurityMgr  mgr = new LdapSecurityMgr();
		try
		{
			mgr.setupProxyPassword(null);
			fail();
		}
		catch(MissingSecurityProperty e)
		{}
		

		Properties props = new Properties();
		props.setProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD, "secret");
		
		String password = mgr.setupProxyPassword(props);
		
		assertEquals("secret", password);
		
		Cryption cryption = new Cryption();
		
		String encryptedText = Cryption.CRYPTION_PREFIX+cryption.encryptText("secret");
		
		props.setProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD, encryptedText);
		
		password = mgr.setupProxyPassword(props);
		
		assertEquals("secret", password);
	}//------------------------------------------------
	@Test
	public synchronized void test_authenticate()
	throws Exception
	{
		LdapSecurityMgr mgr = init();
		
		synchronized (ldapConnectionFactory)
		{

			try
			{
				mgr.authenticate(null);
				fail();
			}
			catch(AuthenticationFailedException e)
			{}
			
			try
			{
				mgr.authenticate(new Properties());
				fail();
			}
			catch(AuthenticationFailedException e)
			{}
			
			Properties props = new Properties();
			props.setProperty("security-username", "admin");
			props.setProperty("security-password", "password");
			mgr.authenticate(props);
			
			
			props.setProperty("security-username", "admin");
			props.setProperty("security-password", "{cryption}rjJzxB9T36rEtzcHtXsChQ==");
			mgr.authenticate(props);
			
			
			
		}
		
	}//------------------------------------------------
	@Test
	public void test_authorize_user()
	throws Exception
	{
		LdapSecurityMgr mgr = init();
		
		Cryption.interpret("test".toCharArray());
		
		synchronized (ldapConnectionFactory)
		{
			String dn = "uid=admin";
			LdapSecurityUser user = new LdapSecurityUser("admin",dn);
			
			ResourcePermission permission  = new ResourcePermission("DATA", "READ");
			boolean bool = mgr.authorize(user, permission);
			assertTrue(bool);
			user = new LdapSecurityUser("unknown","uid=unknown");
			bool = mgr.authorize(user, permission);
			assertFalse(bool);
			
			 permission  = new ResourcePermission("DATA", "READ");
			 user = new LdapSecurityUser("readonly","uid=readonly");
			  bool = mgr.authorize(user, permission);
			  assertTrue(bool);
		}
		
	}//------------------------------------------------
	@Test
	public void test_authorize_group()
	throws Exception
	{
		LdapSecurityMgr mgr = init();
		
		synchronized (ldapConnectionFactory)
		{
			
			LdapSecurityUser user = new LdapSecurityUser("unknown","uid=unknown");
			user.addGroup(new LdapSecurityGroup("readonly", "uid=readonly"));
			ResourcePermission permission  = new ResourcePermission("DATA", "READ");
			boolean bool = mgr.authorize(user, permission);
			assertTrue(bool);
			user = new LdapSecurityUser("unknown","uid=unknown");
			bool = mgr.authorize(user, permission);
			assertFalse(bool);
			
			 permission  = new ResourcePermission("DATA", "READ");
			 user = new LdapSecurityUser("readonly","uid=readonly");
			  bool = mgr.authorize(user, permission);
			  assertTrue(bool);
		}
		
	}//------------------------------------------------

}

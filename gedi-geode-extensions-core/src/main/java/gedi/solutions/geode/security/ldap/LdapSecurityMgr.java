package gedi.solutions.geode.security.ldap;

import java.security.Principal;
import java.util.Properties;
import javax.naming.NamingException;
import org.apache.geode.LogWriter;
import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.security.ResourcePermission;
import gedi.solutions.geode.security.AclSecurityPropertiesDirector;
import gedi.solutions.geode.security.exceptions.MissingSecurityProperty;
import nyla.solutions.core.ds.LDAP;
import nyla.solutions.core.security.data.SecurityAcl;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Cryption;
import nyla.solutions.core.util.Debugger;

/**
 * Security Manager for LDAP based authentication and authorization.
 * 
 * @author Gregory Green
 *
 */
public class LdapSecurityMgr implements org.apache.geode.security.SecurityManager
{

	/**
	 * CRYPTION_KEY_PROP = Cryption.getCryptionKey()
	 */
	public static String CRYPTION_KEY = Cryption.getCryptionKey();
	
	public static org.apache.geode.security.SecurityManager create()
	{
		return new LdapSecurityMgr();
	}//------------------------------------------------

	public LdapSecurityMgr()
	{
		this.securityLogger = null;

	}//------------------------------------------------

	/**
	 * @param principal the principal to authorize
	 * @param context the permission to authorize
	 */
	@Override
	public boolean authorize(Object principal, ResourcePermission context)
	{

		try
		{
			if (principal == null)
			{
				securityLogger.info("Not authorized SecurityManager principal is null for context" + context);
				return false;
			}

			if (context == null)
			{
				securityLogger.info("Not authorized SecurityManager context is null for principal" + principal);
				return true;
			}

			boolean authorized = acl.checkPermission((Principal) principal, toString(context));

			securityLogger
			.fine("principal:" + principal + " context:" + context + " authorized:" + authorized + " acl:" + acl);

			return authorized;

		}
		catch (AuthenticationFailedException e)
		{
			this.securityLogger.warning(e);
			
			throw e;
		}
		catch (RuntimeException e)
		{

			this.securityLogger.warning(e);

			throw new AuthenticationFailedException(e.getMessage() + " STACK:" + Debugger.stackTrace(e));

		}

	}// --------------------------------------------------------------

	@Override
	public void init(final Properties securityProps)
	throws NotAuthorizedException
	{

		Cache cache = CacheFactory.getAnyInstance();

		setup(securityProps, cache);
	}// --------------------------------------------------------------

	String toString(ResourcePermission resourcePermission)
	{

		if (resourcePermission == null)
			return "NULL";

		if (ResourcePermission.ALL_REGIONS.equals(resourcePermission.getRegionName()))
		{
			return new StringBuilder().append(resourcePermission.getResource()).append(":")
			.append(resourcePermission.getOperation()).toString();
		}
		else if (ResourcePermission.ALL_KEYS.equals(resourcePermission.getKey()))
		{
			return new StringBuilder().append(resourcePermission.getResource()).append(":")
			.append(resourcePermission.getOperation()).append(":").append(resourcePermission.getRegionName())
			.toString();
		}
		else
		{
			return new StringBuilder().append(resourcePermission.getResource()).append(":")
			.append(resourcePermission.getOperation()).append(":").append(resourcePermission.getRegionName())
			.append(":").append(resourcePermission.getKey()).toString();
		}

	}//------------------------------------------------
	/**
	 * Set up the security manager
	 * @param securityProps the security properties
	 * @param cache the cache
	 * @throws MissingSecurityProperty when a required property does not exist
	 * 
	 */
	protected void setup(Properties securityProps, Cache cache)
	throws MissingSecurityProperty
	{
		securityLogger = cache.getSecurityLogger();
		
		this.serviceAccountDn = securityProps.getProperty(LdapSecurityConstants.LDAP_PROXY_DN);

		securityLogger.fine(LdapSecurityConstants.LDAP_PROXY_DN + " *************" + serviceAccountDn);

		if (serviceAccountDn == null || serviceAccountDn.length() == 0)
		{
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_PROXY_DN);
		}

		setupProxyPassword(securityProps);

		
		this.ldapUrl = securityProps.getProperty(LdapSecurityConstants.LDAP_SERVER_URL_PROP);
		if (this.ldapUrl == null || this.ldapUrl.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_SERVER_URL_PROP);
			
			
		this.basedn = securityProps.getProperty(LdapSecurityConstants.LDAP_BASEDN_NAME_PROP);
		if (this.basedn == null || this.basedn.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_BASEDN_NAME_PROP);
		
		this.memberOfAttrNm = securityProps.getProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP);
		if (this.memberOfAttrNm == null || this.memberOfAttrNm.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_MEMBEROF_ATTRIB_NM_PROP);
		
		
		this.groupAttrNm = securityProps.getProperty(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP);
		if (this.groupAttrNm == null || this.groupAttrNm.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_GROUP_ATTRIB_NM_PROP);
		
		this.uidAttribute = securityProps.getProperty(LdapSecurityConstants.LDAP_UID_ATTRIBUTE);
		if (this.uidAttribute == null || this.uidAttribute.length() == 0)
		{
			this.uidAttribute = "uid";
		}

		// check to LDAP settings
		try (LDAP ldap = this.ldapConnectionFactory.connect(ldapUrl, serviceAccountDn, proxyPassword.toCharArray()))
		{
		}
		catch (NamingException e)
		{
			securityLogger.warning(e);
			throw new AuthenticationFailedException(e.getMessage(), e);
		}

		AclSecurityPropertiesDirector director =  new AclSecurityPropertiesDirector(securityProps,
		LdapSecurityConstants.LDAP_ACL_GROUP_PREFIX,
		LdapSecurityConstants.LDAP_ACL_USER_PREFIX);
		
		LdapAclBuilder builder = new LdapAclBuilder();
		director.construct(builder);

		this.acl = builder.getAcl();

	}//------------------------------------------------
	/**
	 * 
	 * @param securityProps the properties containing the password
	 */
	String setupProxyPassword(Properties securityProps)
	{
		if(securityProps == null)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD);
		
		this.proxyPassword = securityProps.getProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD);
		if (proxyPassword == null || proxyPassword.length() == 0)
			throw new MissingSecurityProperty(LdapSecurityConstants.LDAP_PROXY_PASSWORD);
		
		this.proxyPassword = Cryption.interpret(proxyPassword);
		
		return this.proxyPassword;
		
	}//------------------------------------------------
	@Override
	public Object authenticate(final Properties props)
	throws AuthenticationFailedException
	{
		if(props == null)
			throw new AuthenticationFailedException(
			"Authentication securities properties not provided");

		String userName = props.getProperty(LdapSecurityConstants.USER_NAME_PROP);

		// securityLogger.info("username is"+ userName);

		if (userName == null)
		{

			throw new AuthenticationFailedException(
			"property ["+ LdapSecurityConstants.USER_NAME_PROP + "] not provided");
		}

		String passwd = props.getProperty(LdapSecurityConstants.PASSWORD_PROP);

		if (passwd == null || passwd.length() == 0)
		{
			throw new AuthenticationFailedException(
			"property ["+ LdapSecurityConstants.PASSWORD_PROP + "] not provided");
		}

		passwd = Cryption.interpret(passwd);
		
		try (LDAP ldap = this.ldapConnectionFactory.connect(this.ldapUrl, this.serviceAccountDn,
		this.proxyPassword.toCharArray()))
		{
			if (ldap == null)
				throw new IllegalArgumentException("ldap is required from factory: "+ldapConnectionFactory.getClass().getName());
			
			return ldap.authenicate(userName, passwd.toCharArray(), this.basedn, uidAttribute, memberOfAttrNm,
			groupAttrNm, timeout);
		}
		catch (NamingException e)
		{
			throw new AuthenticationFailedException(e.getMessage());
		}
		catch (SecurityException e)
		{
			throw new AuthenticationFailedException(e.getMessage());
		}

	}// --------------------------------------------------------------

	/**
	 * 
	 * @param ldapConnectionFactory
	 *            the ldapConnectionFactory to set
	 * 
	 */
	void setLdapConnectionFactory(LDAPConnectionFactory ldapConnectionFactory)
	{
		this.ldapConnectionFactory = ldapConnectionFactory;
	}
	

	
	private String proxyPassword = null;	
	private SecurityAcl acl = null;
	private String basedn = null;
	private String uidAttribute = null;
	private String memberOfAttrNm = "memberOf"; 
	private String groupAttrNm  = null; //Ex: "CN";
	protected LogWriter securityLogger;
	private String serviceAccountDn = null;
	private int timeout = Config.getPropertyInteger("LDAP_TIMEOUT", 10).intValue();
	private LDAPConnectionFactory ldapConnectionFactory = new LDAPConnectionFactory();
	private String ldapUrl;

}

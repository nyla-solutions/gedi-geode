package gedi.solutions.geode.security;

import java.util.Properties;

import nyla.solutions.core.patterns.creational.BuilderDirector;

/**
 * <pre>
 * This class implements the builder design pattern for
 * creating an access control list based on text properties.
 * 
 * Example File create defines three groups 1) admin_grp 2) readonly_cluster_grp 3) readonly_grp
 * and two users 1) data_read_user 2) data_write_user
 * 
 * 
 *  security-ldap-acl-group-admin_grp=ALL
 *  security-ldap-acl-group-readonly_cluster_grp=CLUSTER:READ
 *  security-ldap-acl-group-readonly_grp=READ
 *  security-ldap-acl-user-data_read_user=DATA:READ
 *  security-ldap-acl-user-data_write_user=DATA:WRITE
 *  </pre>
 * @author Gregory Green
 *
 */
public class AclSecurityPropertiesDirector implements BuilderDirector<SecurityAclBuilder>
{
	private final Properties securityProps;

	
	private final String group_prefix;
	private final String user_prefix;

	
	public AclSecurityPropertiesDirector(
				Properties securityProps, 
				String group_prefix,
				String user_prefix)
	{
		super();
		this.securityProps = securityProps;
		
		this.group_prefix = group_prefix;
		this.user_prefix = user_prefix;
	}//------------------------------------------------


	public void construct(SecurityAclBuilder builder)
	{

		securityProps.forEach(
		(k, v) ->
		{
			String keyText = k.toString();
			if (keyText.startsWith(group_prefix))
			{
				String group = keyText.substring(24);
				builder.buildGroupPermission(group, v.toString());
			}
			else if (keyText.startsWith(user_prefix))
			{
				String user = keyText.substring(23);
				builder.buildUserPermission(user, v.toString());
			}
		});

	}

}
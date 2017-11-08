# Overview

The package gedi.solutions.geode.security.ldap contains LDAP based GemFire/Geode security manager.


## Setup LDAP 

For local testing, it is recommended to use [ApacheDS](http://directory.apache.org/apacheds/).

For an easy install on a Mac, it is also recommended to use the [h3nrik/apacheds](https://hub.docker.com/r/h3nrik/apacheds) docker image.

Use the following to build the image

	docker build -t h3nrik/apacheds .

Run the container using the following command

	docker run --name ldap -d -p 389:10389 h3nrik/apacheds


The Apache DS will now be available on port 389. 
The default user/password is admin/secret.

You can use  [ApacheDS Studio](http://directory.apache.org/studio/) the add users for testing.

Also see the following the scripts build the docker image, start Apache DS and add test LDAP users

- src/test/resources/ldap/build.sh 
- src/test/resources/ldap/start.sh  
- src/test/resources/ldap/addusers.sh

## Cluster Startup

1) The CRYPTION_KEY environment variable MUST set 

	export CRYPTION_KEY=GEDI-GEODI

2) Setup GemFire Security Property File


	# LDAP PROXY user DN used to for all authentication LDAP request
	security-ldap-proxy-dn=uid=admin,ou=system
	
	# LDAP PROXY user password (encrypted or un-encrypted passwords supported) 
	security-ldap-proxy-password=secret
	
	# LDAP server URL
	security-ldap-server-url=ldap://localhost:389
	
	# LDAP base dn to search for user for authentication reques
	security-ldap-base-dn=ou=system
	
	# LDAP attribute that will match the user ID
	security-ldap-uid-attribute=uid
	
	# The LDAP  attribute the indicates the users' group associations
	security-ldap-memberOf-attribute=memberOf
	
	# The LDAP GROUP attribute that will match the security-ldap-acl-group-${??} property
	security-ldap-group-attribute=CN
	
	# Example Access Control Lists
	# user nyla has permission to read daata
	
	security-ldap-acl-user-nyla=DATA:READ
	
	# user cluster has permission to performance any cluster operation
	security-ldap-acl-user-cluster=CLUSTER
	
	# user admin ALL permissions
	security-ldap-acl-user-admin=ALL
	security-ldap-acl-group-administrator=ALL
	
	
	# User credentials used to join the GemFire cluster
	security-username=cluster
	security-password={cryption}rjJzxB9T36rEtzcHtXsChQ==


**ACL Permissions**

The Access Control List (ACL) permission are based on the GemFire ResourePermission (Resource:Operation).

Use a security

- ALL - admin level user access with no restrictions
- CLUSTER - all cluster read, write and manage permissions
- CLUSTER:READ - cluster read permission
- CLUSTER:WRITE - cluster write permission
- CLUSTER:MANAGE - cluster management permissions such as shutdown cluster and members
- DATA - all data read, write and manage permissions
- DATA:READ - data read permission
- DATA:WRITE - data write permission
- DATA:MANAGE - data managed permissions such as creating regions
- READ - cluster or data read permissions
- WRITE - cluster or data write permissions

*Password Encryption Support*

You can use the nyla solution core Cryption object to generate an encrypted password. 

Usage java nyla.solutions.core.util.Cryption <pass>

Example:

	java -classpath lib/nyla.solutions.core-1.1.0-SNAPSHOT.jar nyla.solutions.core.util.Cryption password
	{cryption}Hepk7h7LmK3WO+dQlGQB0A==

The encrypted password is always prefixed with {cryption}. This prefixed should be included in the property passwords.

3) ** Start the Locator **

	start locator --name=local --http-service-bind-address=localhost   --http-service-port=7070 --security-properties-file=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/src/test/resources/ldap/gfldapsecurity.properties --J=-Dgemfire.security-manager=gedi.solutions.geode.security.ldap.LdapSecurityMgr --classpath=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-1.0.4.jar:/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/lib/nyla.solutions.core-1.1.0-SNAPSHOT.jar --enable-cluster-configuration
	
	
4) ** Start Servers **

	start server --name=server1 --server-port=9001 --locators=localhost[10334] --security-properties-file=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/src/test/resources/ldap/gfldapsecurity.properties --J=-Dgemfire.security-manager=gedi.solutions.geode.security.ldap.LdapSecurityMgr --classpath=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-1.0.4.jar:/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/lib/nyla.solutions.core-1.1.0-SNAPSHOT.jar
	
	start server --name=server2 --server-port=9002 --locators=localhost[10334] --security-properties-file=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/src/test/resources/ldap/gfldapsecurity.properties --J=-Dgemfire.security-manager=gedi.solutions.geode.security.ldap.LdapSecurityMgr --classpath=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-1.0.4.jar:/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/lib/nyla.solutions.core-1.1.0-SNAPSHOT.jar
	

After startup, gfsh and pulse will require a username/password to connect.
  
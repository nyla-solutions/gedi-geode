package gedi.solutions.geode.client;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.apache.geode.security.AuthInitialize;
import org.junit.Assert;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeConfigAuthInitialize;
import gedi.solutions.geode.client.GeodeSettings;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.util.Config;

/**
 * The VCAP configuration authorization initialization test
 * @author Gregory Green
 *
 */
public class GeodeConfigAuthInitializeTest
{
	@Test
	public void testSecurity_USERNAME_PASSWORD()
	{
		synchronized (GeodeConfigAuthInitializeTest.class)
		{
			System.setProperty("SECURITY_USERNAME","myuser");
			System.setProperty("SECURITY_PASSWORD","mypassword");
		
			Config.reLoad();
			
			GeodeConfigAuthInitialize auth = new GeodeConfigAuthInitialize(new GeodeSettings(""));
			
			assertEquals("myuser", auth.getSecurityUserName());
			assertEquals("mypassword", auth.getSecurityPassword());
			
			
			System.setProperty("security-username","myuser2");
			System.setProperty("security-password","mypassword2");
		
			Config.reLoad();
			
			auth = new GeodeConfigAuthInitialize(new GeodeSettings(""));
			
			assertEquals("myuser2", auth.getSecurityUserName());
			assertEquals("mypassword2", auth.getSecurityPassword());
		}
		
	}
	@Test
	public void testGetCredentials()
	throws Exception
	{
		synchronized (GeodeConfigAuthInitializeTest.class)
		{
			String vcap = IO.readClassPath("json/vcap.json");
			System.setProperty(GeodeSettings.VCAP_SERVICES, vcap);
			
			AuthInitialize auth = new GeodeConfigAuthInitialize(new GeodeSettings(vcap));
			
			Properties in = new Properties();
			
			Properties out = auth.getCredentials(in,null,false);
			
			System.out.println("output properties:"+out);
			
	
			String username = out.getProperty(GeodeConfigAuthInitialize.USER_NAME);
			Assert.assertTrue(username  != null && username.trim().length()> 0);
			Assert.assertNotNull(out.getProperty(GeodeConfigAuthInitialize.PASSWORD));
			
		
		}

	}

}

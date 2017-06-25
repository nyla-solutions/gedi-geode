package gedi.solutions.geode.client;

import java.util.Properties;

import org.apache.geode.security.AuthInitialize;
import org.junit.Assert;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeConfigAuthInitialize;
import gedi.solutions.geode.client.GeodeSettings;
import nyla.solutions.core.io.IO;

/**
 * The VCAP configuration authorization initialization test
 * @author Gregory Green
 *
 */
public class GeodeConfigAuthInitializeTest
{

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
			
			Properties out = auth.getCredentials(in);
			
			System.out.println("output properties:"+out);
			
	
			String username = out.getProperty(GeodeConfigAuthInitialize.USER_NAME);
			Assert.assertTrue(username  != null && username.trim().length()> 0);
			Assert.assertNotNull(out.getProperty(GeodeConfigAuthInitialize.PASSWORD));
			
		
		}

	}

}

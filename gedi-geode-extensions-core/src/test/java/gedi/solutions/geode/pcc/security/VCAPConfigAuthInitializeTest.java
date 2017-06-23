package gedi.solutions.geode.pcc.security;

import java.util.Properties;

import org.apache.geode.security.AuthInitialize;
import org.junit.Assert;
import org.junit.Test;

import gedi.solutions.geode.pcc.config.VCAPConfig;
import nyla.solutions.core.io.IO;

/**
 * The VCAP configuration authorization initialization test
 * @author Gregory Green
 *
 */
public class VCAPConfigAuthInitializeTest
{

	@Test
	public void testGetCredentials()
	throws Exception
	{
		synchronized (VCAPConfigAuthInitializeTest.class)
		{
			String vcap = IO.readClassPath("json/vcap.json");
			System.setProperty(VCAPConfig.VCAP_SERVICES, vcap);
			
			AuthInitialize auth = new VCAPConfigAuthInitialize(new VCAPConfig());
			
			Properties in = new Properties();
			
			Properties out = auth.getCredentials(in);
			
			System.out.println("output properties:"+out);
			
	
			String username = out.getProperty(VCAPConfigAuthInitialize.USER_NAME);
			Assert.assertTrue(username  != null && username.trim().length()> 0);
			Assert.assertNotNull(out.getProperty(VCAPConfigAuthInitialize.PASSWORD));
			
		
		}

	}

}

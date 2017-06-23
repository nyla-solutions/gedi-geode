package gedi.solutions.geode.pcc.config;

import org.junit.Assert;
import org.junit.Test;

import nyla.solutions.core.io.IO;

public class VCAPConfigTest
{

	@Test
	public void testGetInstance()
	throws Exception
	{
		
		
		
		VCAPConfig config = VCAPConfig.getInstance();
		
		Assert.assertNotNull(config);
		
		Assert.assertNull(config.getLocators());
		Assert.assertNull(config.getLocatorUrlList());
		Assert.assertNull(config.getSecuredToken());
	}//------------------------------------------------

	@Test
	public void testGetLocators()
	throws Exception
	{
		String envContent = IO.readClassPath("json/vcap.json");
		VCAPConfig config = new VCAPConfig(envContent);
		
		String locators = config.getLocators();
		System.out.println("locators:"+locators);
		Assert.assertNotNull(locators);
		Assert.assertEquals("10.244.0.4[55221],10.244.1.2[55221],10.244.0.130[55221]",locators);
	}//------------------------------------------------

	@Test
	public void testGetUsername()
	throws Exception
	{
		String envContent = IO.readClassPath("json/vcap.json");
		VCAPConfig config = new VCAPConfig(envContent);
		
		String token = null;
		Assert.assertNull(config.getSecuredToken("invalid", token));
		Assert.assertEquals("developer", config.getSecuredToken("developer", token).getName());
		Assert.assertEquals("cluster_operator", config.getSecuredToken("cluster_operator", token).getName());
		
		Assert.assertNotNull(config.getSecuredToken(null));
	}//------------------------------------------------
	/**
	 * Test Get password
	 * @throws Exception the exception
	 */
	@Test
	public void testGetPassword()
	throws Exception
	{
		String envContent = IO.readClassPath("json/vcap.json");
		VCAPConfig config = new VCAPConfig(envContent);
		
		String token = null;
		Assert.assertArrayEquals("some_developer_password".toCharArray(), config.getSecuredToken("developer", token).getCredentials());
		Assert.assertArrayEquals("some_password".toCharArray(), config.getSecuredToken("cluster_operator", token).getCredentials());
	
	}

}

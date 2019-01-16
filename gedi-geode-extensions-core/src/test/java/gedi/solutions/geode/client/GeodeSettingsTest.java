package gedi.solutions.geode.client;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;

import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import gedi.solutions.geode.client.GeodeSettings;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.util.Config;

public class GeodeSettingsTest
{
	@Test
	public void testGetLocatorUrlList() throws Exception
	{
		System.setProperty(GeodeConfigConstants.LOCATORS_PROP, "host1[1],host2[2],host2[3];");
		
		Config.reLoad();
		
		List<URI> list = GeodeSettings.getInstance().getLocatorUrlList();
		assertTrue(list != null && !list.isEmpty());
		
	}
	@Test
	public void testLocatorsBuild() throws Exception
	{
		try
		{
			System.setProperty(GeodeConfigConstants.LOCATORS_PROP, "host1[1],host2[2],host2[3]");
			
			Config.reLoad();
			
			List<URI> list = GeodeSettings.getInstance().getLocatorUrlList();
			assertTrue(list != null && !list.isEmpty());
			
			assertEquals(3,list.size());
			
			
			ClientCacheFactory factory  = mock(ClientCacheFactory.class);
			GeodeSettings.getInstance().constructPoolLocator(factory);
			
			assertNotNull(factory);
			verify(factory,Mockito.atLeastOnce()).addPoolLocator(anyString(), anyInt());
			verify(factory,times(3)).addPoolLocator(anyString(), anyInt());
			
		
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testGetInstance()
	throws Exception
	{
		
		
		
		GeodeSettings config = GeodeSettings.getInstance();
		
		Assert.assertNotNull(config);
		
	}//------------------------------------------------

	@Test
	public void testGetLocators()
	throws Exception
	{
		String envContent = IO.readClassPath("json/vcap.json");
		GeodeSettings config = new GeodeSettings(envContent);
		
		String locators = config.getLocators();
		System.out.println("locators:"+locators);
		Assert.assertNotNull(locators);
		Assert.assertEquals("10.244.0.4[55221],10.244.1.2[55221],10.244.0.130[55221]",locators);
	}//------------------------------------------------

	@Test
	public void testBlankEnvContent() throws Exception
	{
		String envContent = null;
		GeodeSettings config = new GeodeSettings(envContent);
		
		String envLocatorHost = System.getenv(GeodeConfigConstants.LOCATOR_HOST_PROP);
		
		if(envLocatorHost == null || envLocatorHost.length() == 0)
		{
			List<URI> l = config.getLocatorUrlList();
			assertTrue("getLocatorHost:"+config.getLocatorUrlList()+" would be empty",
			l == null || l.size() == 0); 
			
			
			envContent = " ";
			config = new GeodeSettings(envContent);
			
			l = config.getLocatorUrlList();
			assertTrue("getLocatorHost:"+l+" would be empty",l == null || l.size() == 0); 

		}
		else
		{
			assertEquals(envLocatorHost, GeodeSettings.getInstance().getLocatorHost());
		}
		
	}//------------------------------------------------
	@Test
	public void testGetUsername()
	throws Exception
	{
		String envContent = IO.readClassPath("json/vcap.json");
		GeodeSettings config = new GeodeSettings(envContent);
		
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
		GeodeSettings config = new GeodeSettings(envContent);
		
		String token = null;
		Assert.assertArrayEquals("some_developer_password".toCharArray(), config.getSecuredToken("developer", token).getCredentials());
		Assert.assertArrayEquals("some_password".toCharArray(), config.getSecuredToken("cluster_operator", token).getCredentials());
	
	}

}

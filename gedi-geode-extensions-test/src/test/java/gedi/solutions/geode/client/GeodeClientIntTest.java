package gedi.solutions.geode.client;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.Assert;
import org.junit.Test;

import nyla.solutions.core.util.Config;

public class GeodeClientIntTest
{

	@Test
	public void testGeodeClientBooleanStringArray()
	{
		GeodeClient geodeClient = new GeodeClient(false, ".*");
		Assert.assertTrue(geodeClient.getClientCache() != null);
	}

	@Test
	public void testGeodeClientStringIntBooleanStringArray()
	{
		GeodeClient geodeClient = new GeodeClient("localhost",10334,false, ".*");
		Assert.assertTrue(geodeClient.getClientCache() != null);
	}

	@Test
	public void testGeodeClientClientCache()
	{
		ClientCache clientCache = ClientCacheFactory.getAnyInstance();
		GeodeClient geodeClient = new GeodeClient(clientCache);
		
		Assert.assertTrue(geodeClient.getClientCache() != null);
	}

	@Test
	public void testGeodeClientClientCacheClientRegionFactoryOfQQ()
	{
		ClientCache clientCache = ClientCacheFactory.getAnyInstance();
		GeodeClient geodeClient = new GeodeClient(clientCache,null);
		
		Assert.assertTrue(geodeClient.getClientCache() != null);
		
		
	}

	@Test
	public void testGeodeClientConnect()
	{
		System.setProperty("LOCATOR_HOST", "localhost");
		System.setProperty("LOCATOR_PORT", "10334");
		Config.reLoad();
		
		GeodeClient geodeClient = GeodeClient.connect();
		Assert.assertNotNull(geodeClient.getRegion("Test"));
		Assert.assertTrue(geodeClient.getClientCache() != null);
	}
}

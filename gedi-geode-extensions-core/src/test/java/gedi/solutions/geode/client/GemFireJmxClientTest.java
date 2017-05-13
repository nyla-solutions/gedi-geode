package gedi.solutions.geode.client;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import nyla.solutions.core.patterns.jmx.JMX;

//@Ignore
public class GemFireJmxClientTest
{

	//@Test
	public void testLocator()
	{
		//52.54.153.210 11099
		//JMX jmx = JMX.connect("localhost", 1099);
		//54.198.131.183
		
		ClientCacheFactory  factory = new ClientCacheFactory().addPoolLocator("52.91.22.126", 10000)
				.set("log-level", "fine").set("log-file", "runtime/client.log");
		ClientCache cache = factory.create();
		
		Region<Object, Object> region = cache
				.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("Test");
		
		//Address, UserProfile
		//Region<String,String> region = cache.getRegion("Test", jmx);
		
		
		region.put("greg", "hello");
		
		Assert.assertEquals("hello",region.get("greg"));
	}

	@Test
	public void testGetRegion()
	{
		//52.54.153.210 11099
		JMX jmx = JMX.connect("localhost", 1099);
		//54.198.131.183
		
		//JMX jmx = JMX.connect("54.198.105.20", 11099);
		
		//Address, UserProfile
		Region<String,String> region = GemFireJmxClient.getRegion("Inventories", jmx);
		
		region.put("greg", "hello");
		
		Assert.assertEquals("hello",region.get("greg"));
	}

}

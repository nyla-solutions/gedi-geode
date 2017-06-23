package gedi.solutions.geode.client;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import nyla.solutions.core.patterns.jmx.JMX;

//@Ignore
/**
 * 
 * locator-idp-606068407.us-west-2.elb.amazonaws.com
 * cache-idp-104800883.us-west-2.elb.amazonaws.com
 * cache2-idp-1239450408.us-west-2.elb.amazonaws.com
 * 
 * @author Gregory Green
 *
 */
public class GemFireJmxClientTest
{
	static ClientCacheFactory  factory = null;
	static ClientCache cache  = null;
	static Region<Object, Object> region= null;
	
	//@BeforeClass
	public static void setUp()
	{
		
		//factory = new ClientCacheFactory().addPoolLocator("ec2-52-27-12-106.us-west-2.compute.amazonaws.com", 10000)
		//locator2-idp-979146816.us-west-2.elb.amazonaws.com
		factory = new ClientCacheFactory().addPoolLocator("locator2-idp-979146816.us-west-2.elb.amazonaws.com", 10000)
				.set("log-level", "fine").set("log-file", "target/client.log");
		cache = factory.create();
		
		region = cache
		.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY).create("Test");
	}



	//@Test
	public void test1Put()
	{
		
	    region.put("world", "hello");
		
		Assert.assertEquals("hello",region.get("world"));
		
		System.out.println(region.get("world"));
	}
	
	//@Test
	public void test2Get()
	{
		
		region = cache.getRegion("Test");
		Assert.assertEquals("hello",region.get("world"));
		
		System.out.println(region.get("world"));
	}

	//@Test
	public void testGetRegion()
	{
		//52.54.153.210 11099
		JMX jmx = JMX.connect("54.69.23.36", 11099);
		//54.198.131.183
		
		//JMX jmx = JMX.connect("54.198.105.20", 11099);
		
		//Address, UserProfile
		Region<String,String> region = GemFireJmxClient.getRegion("Inventories", jmx);
		
		region.put("greg", "hello");
		
		Assert.assertEquals("hello",region.get("greg"));
	}

}

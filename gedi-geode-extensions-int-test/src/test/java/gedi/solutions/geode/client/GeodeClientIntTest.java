package gedi.solutions.geode.client;

import static org.junit.Assert.*;

import java.util.Collection;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import gedi.solutions.geode.io.search.TextPageCriteria;
import nyla.solutions.core.security.user.data.User;
import nyla.solutions.core.security.user.data.UserProfile;
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
	public void test_SearchTextWithPages() throws Exception
	{
		GeodeClient geodeClient = GeodeClient.connect();
		
		Region<String,Collection<?>> pageRegion = geodeClient.getRegion("PAGING");
		assertNotNull(pageRegion);
		
		Region<Object,Object> userTestRegion = geodeClient.getRegion("USER_TEST");
		assertNotNull(userTestRegion);
		
		TextPageCriteria criteria = new TextPageCriteria();
		criteria.setRegionName("USER_TEST");
		
		///put some data
		String email ="nyl2@test.com", loginID ="nyla",firstName = "nyla", lastName = "green";
		userTestRegion.put("nyla", new UserProfile(email,loginID,firstName,lastName));
		
		criteria.setQuery("greg");
		
		Collection<String> pages = geodeClient.searchWithPageKeys(criteria);
		
		assertNotNull(pages);
		
		assertTrue(!pages.isEmpty());
		
		
		//get pages
		Collection<User> users = geodeClient.researchSearchResults(criteria, 0);
		
		assertTrue(users != null && !users.isEmpty());
		
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
	
	//@Test
	@Ignore
	public void testPutAmmountOfData()
	{
		
		GeodeClient geodeClient = GeodeClient.connect();
		
		int i = 0;
		while(true)
		{
			i ++;
			geodeClient.getRegion("TEST_PARTITION_PERSISTENT_OVERFLOW").put(i, new UserProfile());
			
		}
		
	}
	
	
}

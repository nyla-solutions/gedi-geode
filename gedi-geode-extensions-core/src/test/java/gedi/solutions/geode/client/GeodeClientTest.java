package gedi.solutions.geode.client;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.Ignore;
import org.junit.Test;
import java.util.Properties;

/**
 * Created by ggreen on 7/7/17.
 */

public class GeodeClientTest  {

    @Test
    @Ignore
    public void testing_security()
            throws Exception
    {
    	Properties prop = new Properties();
    	prop.setProperty("security-username", "paul");
    	prop.setProperty("security-password", "admin");
    	
    	ClientCacheFactory factory = new ClientCacheFactory(prop)
    	.addPoolLocator("localhost", 10334);
    	//.addPoolLocator("localhost", 10334)
    	//.set("locators", "localhost[10334")
    	
    	
    	
    	ClientCache cache = factory.create();
    	
    	Region<Object,Object> region = 
    	cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("TEST");
    	
    	region.put("test", "tesT");
    	
    	
    }
            
//    @Test
//    public void geodeClient_ShouldQueueReturnResultsForCq()
//            throws Exception
//    {
//
//        PdxInstance expected = null;
//        ClientRegionFactory<?,?> factory = Mockito.mock(ClientRegionFactory.class);
//
//        ClientCache clientCache = Mockito.mock(ClientCache.class);
//
//        QueryService queryService = Mockito.mock(QueryService.class);
//
//        CqQuery cqQuery =  Mockito.mock(CqQuery.class);
//        Mockito.when(clientCache.getQueryService()).thenReturn(queryService);
//        Mockito.when(queryService.newCq(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(cqQuery);
//       // Mockito.when(cqQuery.)
//
//
//        GeodeClient geodeClient = new GeodeClient(clientCache,factory);
//
//        String cnName  = "myname";
//        String oql = "select * from /my region";
//
//        Queue<PdxInstance> queue = geodeClient.registerCq(cnName,oql);
//
//        Assert.assertTrue(queue != null);
//
//        PdxInstance pdxRow = queue.poll();
//
//        Assert.assertEquals(expected,pdxRow);
//
//
//
//
//
//
//    }
}

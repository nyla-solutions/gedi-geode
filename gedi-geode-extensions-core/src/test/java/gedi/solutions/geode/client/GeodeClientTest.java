package gedi.solutions.geode.client;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

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
    	.addPoolLocator("ec2-18-218-46-170.us-east-2.compute.amazonaws.com", 10334);
    	//.addPoolLocator("localhost", 10334)
    	//.set("locators", "localhost[10334")
    	
    	
    	
    	ClientCache cache = factory.create();
    	
    	Region<Object,Object> region = 
    	cache.createClientRegionFactory(ClientRegionShortcut.PROXY).create("test");
    	
    	region.put("test", "tesT");
    	
    	
    }
    
    //@Test
    public void testWithCachingProxy()
    {
    	
    		GeodeClient geodeClient = new GeodeClient("localhost",10000,true,".*");
    		
    		Region<String,Object> region = geodeClient.getRegion("test");
    		region.put("1", "1");
    		
    		assertEquals("1", region.get("1"));
    		
    	
    }
    
    @Test
	public void testPdxSerializer() throws Exception
	{
		PdxSerializer pdxSerializer = GeodeClient.createPdxSerializer(ReflectionBasedAutoSerializer.class.getName(), GeodeConfigConstants.PDX_CLASS_PATTERN);
		System.out.println("pdxSerializier"+pdxSerializer);
    	    assertNotNull(pdxSerializer);
    	    assertTrue(pdxSerializer instanceof ReflectionBasedAutoSerializer);

    	    String[] pattern = {".*"};
    	    PdxSerializer pdxSerializerVerifier = GeodeClient.createPdxSerializer(TestPdxSerialzier.class.getName(),pattern);
    	    
    	    assertTrue(pdxSerializerVerifier instanceof TestPdxSerialzier);
	}
    
    public static class TestPdxSerialzier implements PdxSerializer
	{
    	
    		public TestPdxSerialzier(String... args)
    		{}
		
		@Override
		public boolean toData(Object o, PdxWriter out)
		{
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public Object fromData(Class<?> clazz, PdxReader in)
		{
			// TODO Auto-generated method stub
			return null;
		}
	};
            
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

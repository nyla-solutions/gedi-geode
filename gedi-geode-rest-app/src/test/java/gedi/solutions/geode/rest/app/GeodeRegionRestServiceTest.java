package gedi.solutions.geode.rest.app;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.apache.geode.cache.Region;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeClient;

public class GeodeRegionRestServiceTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void testPutGet()
	{
		String region = null;
		String key = null;
		String value = null;
		
		GeodeClient geodeClient = mock(GeodeClient.class);
		Region<Object,Object> geodeRegion = mock(Region.class);
		when(geodeClient.getRegion(anyString())).thenReturn(geodeRegion);
		
		GeodeRegionRestService service = new GeodeRegionRestService();
		service.geode = geodeClient;
		
		String results = service.putEntry(region, key, value);
		results = service.getValueByKey(region, key);
		
		assertNull(results);
		
		region = "test";
		results = service.putEntry(region, key, value);
		results = service.getValueByKey(region, key);
		assertNull(results);
		
		key = "key";
		results = service.putEntry(region, key, value);
		results = service.getValueByKey(region, key);
		assertNull(results);
		
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testRemove()
	{
		String region = null;
		String key = null;
		String value = null;
		
		
		
		GeodeClient geodeClient = mock(GeodeClient.class);
		Region<Object,Object> geodeRegion = mock(Region.class);
		when(geodeClient.getRegion(anyString())).thenReturn(geodeRegion);
		
		GeodeRegionRestService service = new GeodeRegionRestService();
		service.geode = geodeClient;
		
		String results = service.putEntry(region, key, value);
		results = service.getValueByKey(region, key);
		
		assertNull(results);
		
		region = "test";
		results = service.delete(region, key);
		results = service.getValueByKey(region, key);
		assertNull(results);
			
		
	}

}

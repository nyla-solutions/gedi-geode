package gedi.solutions.geode.client;


import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Collection;

import org.apache.geode.cache.Region;
import org.junit.BeforeClass;
import org.junit.Test;

import gedi.solutions.geode.client.data.QaDataDomain;
import gedi.solutions.geode.io.QuerierService;
import nyla.solutions.core.util.Text;

public class GeodeRestAppIT
{
	
	private static Region<String,QaDataDomain> region;
	private static QuerierService query;
	
	@BeforeClass
	public static void setUp()
	{
		GeodeClient client = GeodeClient.connect();
		
		region = client.getRegion("test");
		
		query = client.getQuerierService();
	}
	
	
	@Test
	public void testPdxTypes() throws Exception
	{
		QaDataDomain domain = new QaDataDomain();
		domain.setMyDate(Calendar.getInstance().getTime());
		domain.setMyString(Text.generateId());
		
		region.put(domain.getMyString(), domain);
		
		QaDataDomain user = region.get(domain.getMyString());
		
	
		assertEquals(domain.getMyString(),user.getMyString());
		
	}
	
	@Test
	public void testQueries() throws Exception
	{
		
		QaDataDomain domain = new QaDataDomain();
		domain.setMyDate(Calendar.getInstance().getTime());
		domain.setMyString(Text.generateId());
		
		region.put(domain.getMyString(), domain);
		
		QaDataDomain user = region.get(domain.getMyString());
		
	
		assertEquals(domain.getMyString(),user.getMyString());
		
		Collection<QaDataDomain> collection = query.query("select * from /test where myString = $1",domain.getMyString());
		
		assertTrue(collection != null && !collection.isEmpty());
		
		assertEquals(collection.iterator().next().getMyString(), domain.getMyString());;
		
		
		
	}

}

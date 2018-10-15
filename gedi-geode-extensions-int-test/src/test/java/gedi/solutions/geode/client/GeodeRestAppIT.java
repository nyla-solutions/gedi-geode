package gedi.solutions.geode.client;


import static org.junit.Assert.*;

import java.util.Calendar;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.junit.Test;

import gedi.solutions.geode.client.data.QaDataDomain;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.core.util.Text;

public class GeodeRestAppIT
{
	
	@Test
	public void testPdxTypes() throws Exception
	{
		QaDataDomain domain = new QaDataDomain();
		domain.setMyDate(Calendar.getInstance().getTime());
		domain.setMyString(Text.generateId());
		
		Region<String,Object> region = GeodeClient.connect().getRegion("pdxTest");
		
		region.put(domain.getMyString(), domain);
		
		PdxInstance user = (PdxInstance)region.get(domain.getMyString());
		
		String json = JSONFormatter.toJSON(user);
		
		
	}

}

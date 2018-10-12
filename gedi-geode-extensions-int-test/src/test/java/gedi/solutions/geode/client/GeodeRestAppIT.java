package gedi.solutions.geode.client;


import static org.junit.Assert.*;

import org.apache.geode.cache.Region;
import org.apache.geode.pdx.PdxInstance;
import org.junit.Test;

import nyla.solutions.core.security.user.data.UserProfile;

public class GeodeRestAppIT
{
	
	@Test
	public void testCRUD() throws Exception
	{
		Region<String,PdxInstance> region = GeodeClient.connect().getRegion("test");
		
		///PdxInstanceFactory f = GeodeClient.connect().getClientCache().createPdxInstanceFactory(UserProfile.class.getName());
		
		//f.writeString("email","nyla@solutions.io");
		
		//region.put("ggreen", f.create());
		
		PdxInstance user = region.get("rgreen");
		
		assertEquals("nyla@solutions.io",((UserProfile)user.getObject()).getEmail());
		
	}

}

package gedi.solutions.geode.client.csv;

import static org.junit.Assert.*;

import org.apache.geode.cache.Region;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeClient;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import nyla.solutions.core.security.user.data.UserProfile;

public class CsvIT
{
	
	@Test
	public void testName() throws Exception
	{
		
		Region<String,UserProfile> region = GeodeClient.connect().getRegion("testCsv");
		
		UserProfile user = new UserProfile();
		
		JavaBeanGeneratorCreator<UserProfile> factory = new JavaBeanGeneratorCreator<>
		(UserProfile.class).randomizeAll();
		
		
		for (int i = 0; i < 10; i++)
		{
			user = factory.create();
			region.put(user.getLoginID(),user);
			
		}
	}

}

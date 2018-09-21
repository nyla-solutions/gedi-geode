package gedi.solutions.geode.qa.performance;

import org.apache.geode.cache.Region;
import org.junit.Test;

import nyla.solutions.core.security.user.data.UserProfile;

import static org.mockito.Mockito.*;

public class PutRegionJavaBeanLoadRunnerTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void test()
	{
		Region<String, UserProfile> region = mock(Region.class);
		
		int count = 1;
		
		Runnable runner = new PutRegionJavaBeanLoadRunner<UserProfile>(count,region,UserProfile.class,1);
		
		runner.run();
		
		verify(region).put(anyString(), any());
		
		
	}

}

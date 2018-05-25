package gedi.solutions.geode.office;

import static org.junit.Assert.*;

import org.junit.Test;
import static org.mockito.Mockito.*;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;

public class StatsUtilTest
{
	@Test
	public void testAppName()
	{
		assertNull(StatsUtil.getAppName(null));
		
		ResourceInst resourceInst = mock(ResourceInst.class);
		when(resourceInst.getName()).thenReturn("server1");
		
		ResourceType rt = mock(ResourceType.class);
		
		
		when(resourceInst.getType()).thenReturn(rt);
		when(rt.getName()).thenReturn(StatsConstants.CACHE_SERVER_STAT_NM);
		
		ResourceInst[] resources = {resourceInst};
		
		
		assertEquals("server1",StatsUtil.getAppName(resources));
		
		
	}
	
	@Test
	public void testFormatMachine()
	{
		assertEquals("server", StatsUtil.formatMachine("server"));
	}

}

package gedi.solutions.geode.office;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;

public class StatsUtil
{

	public static String formatMachine(String machine)
	{

		return machine;
	}
	/**
	 * Current supports get cache server name
	 * Determine the logic name of app
	 * @param resources the resource used to determine namne
	 * @return the application name (ex: datanode or locator name)
	 */
	public static String getAppName(ResourceInst[] resources)
	{
		if(resources == null)
			return null;
		
		ResourceType rt = null;
		for (ResourceInst resourceInst : resources)
		{
			rt = resourceInst.getType();
			if(rt == null)
				continue;
			
			if(!"CacheServerStats".equals(rt.getName()))
				continue;
			
			return resourceInst.getName();
		}
		return null;
	}


}

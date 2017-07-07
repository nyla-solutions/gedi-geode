package gedi.solutions.geode.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;

/**
 * Use the client cache to obtain region objects
 * @author Gregory Green
 *
 */
public class ClientRegionDictionary implements RegionDictionary
{


	/* (non-Javadoc)
	 * @see com.jnj.lpfg.lpfg_functions.io.RegionDictionary#getRegion(java.lang.String)
	 */
	public <K, V> Region<K, V> getRegion(String name)
	{
		ClientCache cache = ClientCacheFactory.getAnyInstance();
		
		
		
		return cache.getRegion(name);
		
	}

}

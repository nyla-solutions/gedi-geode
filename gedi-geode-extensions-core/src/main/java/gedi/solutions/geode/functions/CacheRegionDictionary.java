package gedi.solutions.geode.functions;

import java.io.Serializable;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;


/**
 * @author Gregory Green
 *
 */
public class CacheRegionDictionary implements RegionDictionary, Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9067270223076402385L;

	/* (non-Javadoc)
	 * @see com.jnj.lpfg.lpfg_functions.io.RegionDictionary#getRegion(java.lang.String)
	 */
	@Override
	public <K, V> Region<K, V> getRegion(String name)
	{
		return CacheFactory.getAnyInstance().getRegion(name);
	}

}

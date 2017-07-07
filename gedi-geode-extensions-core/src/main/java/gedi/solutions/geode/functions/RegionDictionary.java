package gedi.solutions.geode.functions;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.partition.PartitionRegionHelper;

public interface RegionDictionary
{
	public <K,V> Region<K, V> getRegion(String name);
	
	/**
	 * 
	 * @param region the region
	 * @return the local data
	 */
	  default <K,V> Region<K,V> getLocalData(Region<K,V> region) 
	  {
		  if(!DataPolicy.EMPTY.equals(region.getAttributes().getDataPolicy()))
				  return PartitionRegionHelper.getLocalData(region);
		  
		  return region;
	
	  } 

}

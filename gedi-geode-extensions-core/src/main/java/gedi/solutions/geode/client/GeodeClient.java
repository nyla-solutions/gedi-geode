package gedi.solutions.geode.client;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;


/**
 * Example GemFire (power by Apache Geode) API wrapper
 * @author Gregory Green
 *
 */
public class GeodeClient
{
	
	/**
	 * GemFire connection object is a Client Cache
	 */
	private final ClientCache clientCache;
	private final boolean cachingProxy;
	private final ClientRegionFactory<?, ?> factory;
	
	public GeodeClient(String host, int port, boolean cachingProxy, String... classPatterns)
	{
		this.cachingProxy = cachingProxy;
		
		if(cachingProxy)
		{
			//Caching Proxy
			this.clientCache = new ClientCacheFactory().addPoolLocator(host, port)
			.setPoolSubscriptionEnabled(true)
			.setPdxSerializer(new ReflectionBasedAutoSerializer(classPatterns))
			.set("log-level", "fine")
			.set("log-file", "target/client.log")
			.create();
			factory = clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
		}
		else
		{
			this.clientCache = new ClientCacheFactory().addPoolLocator(host, port)
			.setPdxSerializer(new ReflectionBasedAutoSerializer(classPatterns))
			.set("log-level", "fine")
			.set("log-file", "target/client.log")

			.create();
			factory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
			
		}
	}//------------------------------------------------
	/**
	 * 
	 * @param clientCache the connection
	 */
	public GeodeClient(ClientCache clientCache)
	{
		this(clientCache,
		clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY));
	}//------------------------------------------------

	public GeodeClient(ClientCache clientCache,  ClientRegionFactory<?, ?> factory)
	{
		cachingProxy = false;
		this.clientCache = clientCache;
		this.factory = factory;
	}
	
	/**
	 * This is an example to get or create a region
	 * @param regionName the  name
	 * @return the existing or created region (using the ClientbRegionFactory)
	 */
	@SuppressWarnings("unchecked")
	public <K,V> Region<K,V> getRegion(String regionName)
	{
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return (Region<K,V>)region;
		
		region = (Region<K,V>)this.factory.create(regionName);
		
		//Client side data policy is typically NORMAL or EMPTY
		if(cachingProxy)
		{
			//NORMAL data policy are typically used for CACHING_PROXY
			//You should interest so updates for the server will be pushed to the clients
			region.registerInterestRegex(".*");
		}
		
		return region;
	}//------------------------------------------------
}

package gedi.solutions.geode.client;

import java.net.URI;
import java.util.List;
import java.util.Properties;
import java.util.Queue;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqClosedException;
import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.CqExistsException;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.QueryInvalidException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.RegionNotFoundException;
import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import gedi.solutions.geode.client.cq.CqQueueListener;
import nyla.solutions.core.util.Config;


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
	
	public GeodeClient(boolean cachingProxy, String... classPatterns)
	{
		 this(GeodeSettings.getInstance().getLocatorHost(),
		 	GeodeSettings.getInstance().getLocatorPort(),
		 	cachingProxy, 
		 	classPatterns);
	}//------------------------------------------------
	public GeodeClient(String host, int port, boolean cachingProxy, String... classPatterns)
	{
		this.cachingProxy = cachingProxy;
		
		 Properties props = new Properties();
		props.setProperty("security-client-auth-init", GeodeConfigAuthInitialize.class.getName()+".create");
		
		String name = Config.getProperty("name",GeodeClient.class.getSimpleName());
		
		this.clientCache = new ClientCacheFactory(props).addPoolLocator(host, port)
		.setPoolSubscriptionEnabled(true)
		.setPdxSerializer(new ReflectionBasedAutoSerializer(classPatterns))
		.set("log-level", Config.getProperty("log-level","config"))
		.set("log-file", Config.getProperty("log-file","client.log"))
		.set("name", name)
		.create();
		
		if(cachingProxy)
		{
			//Caching Proxy
			factory = clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
		}
		else
		{
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
	public <T> Queue<T> registerCq(String cqName,String oql) 
	{
		try
		{
			QueryService queryService = this.clientCache.getQueryService();
			

			// Create CqAttribute using CqAttributeFactory
			CqAttributesFactory cqf = new CqAttributesFactory();

			// Create a listener and add it to the CQ attributes callback defined below
			CqQueueListener<T> cqListener = new CqQueueListener<T>();
			cqf.addCqListener(cqListener);
			CqAttributes cqa = cqf.create();
			// Name of the CQ and its query
			
			// Create the CqQuery
			CqQuery cqQuery = queryService.newCq(cqName, oql, cqa);
			
			cqListener.setCqQuery(cqQuery);

			// Execute CQ, getting the optional initial result set
			// Without the initial result set, the call is priceTracker.execute();
			cqQuery.execute();
			
			return cqListener;
		}
		catch (CqException| CqClosedException |RegionNotFoundException |QueryInvalidException | CqExistsException  e)
		{
		  throw new nyla.solutions.core.exception.SystemException
		  ("ERROR:"+e.getMessage()+" cqName:"+cqName+" oql:"+oql,e);
		}
	}
	//------------------------------------------------
	public static GeodeClient connect()
	{
	 GeodeSettings vcap = GeodeSettings.getInstance();
	
	 List<URI> locatorList = vcap.getLocatorUrlList();
	
	 URI locatorURI = null;
	
	if(locatorList != null && !locatorList.isEmpty())
			locatorURI = locatorList.get(0);
	
	int port = 0;
	
	String host = Config.getProperty("LOCATOR_HOST");
	
	if (host.trim().length() == 0)
	{
		//check env
		if(locatorURI != null)
		{
			host = locatorURI.getHost();
			port = locatorURI.getPort();
		}
	}
	else
	{
		port = Config.getPropertyInteger("LOCATOR_PORT",10334);
	}
	
	 String name = Config.getProperty("name",GeodeClient.class.getSimpleName());
	

		 Properties props = new Properties();
		props.setProperty("security-client-auth-init", GeodeConfigAuthInitialize.class.getName()+".create");
		
		ClientCacheFactory factory = new ClientCacheFactory(props)
		.addPoolLocator(host, port)
		.setPoolSubscriptionEnabled(true)
		.setPdxReadSerialized(true)
		.set("name", name);
		
		return new GeodeClient(factory.create());
	}//------------------------------------------------
	/**
	 * @return the clientCache
	 */
	public ClientCache getClientCache()
	{
		return clientCache;
	}
	
}

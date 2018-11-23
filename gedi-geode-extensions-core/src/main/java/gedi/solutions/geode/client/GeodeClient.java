package gedi.solutions.geode.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.function.Consumer;

import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.CqAttributes;
import org.apache.geode.cache.query.CqAttributesFactory;
import org.apache.geode.cache.query.CqClosedException;
import org.apache.geode.cache.query.CqException;
import org.apache.geode.cache.query.CqExistsException;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.QueryInvalidException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.RegionNotFoundException;
import org.apache.geode.pdx.PdxSerializer;
import gedi.solutions.geode.client.cq.CqQueueListener;
import gedi.solutions.geode.io.GemFireIO;
import gedi.solutions.geode.io.Querier;
import gedi.solutions.geode.io.QuerierMgr;
import gedi.solutions.geode.io.QuerierService;
import gedi.solutions.geode.listeners.CacheListenerBridge;
import gedi.solutions.geode.lucene.GeodeLuceneSearch;
import gedi.solutions.geode.lucene.TextPageCriteria;
import gedi.solutions.geode.lucene.function.LuceneSearchFunction;
import gedi.solutions.geode.serialization.EnhancedReflectionSerializer;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.patterns.iteration.Paging;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;



/**
 *  GemFire (power by Apache Geode) API wrapper.
 *  
 *  export SSL_KEYSTORE_PASSWORD=pivotal
	export SSL_PROTOCOLS=TLSv1.2
	export SSL_TRUSTSTORE_PASSWORD=pivotal
	export SSL_KEYSTORE_TYPE=jks
	export SSL_CIPHERS=TLS_RSA_WITH_AES_128_GCM_SHA256
	export SSL_ENABLED_COMPONENTS=gateway,server,locator,jmx
	export SSL_REQUIRE_AUTHENTICATION=true
	export SSL_TRUSTSTORE_CLASSPATH_FILE=truststore.jks
	export SSL_KEYSTORE_CLASSPATH_FILE=keystore.jks

 * @author Gregory Green
 *
 */
public class GeodeClient
{
	
	/**
	 * GemFire connection object is a Client Cache
	 */
	private final ClientCache clientCache;
	private boolean cachingProxy;
	private final ClientRegionFactory<?, ?> proxyRegionfactory;
	private final ClientRegionFactory<?, ?> cachingRegionfactory;
	private static GeodeClient geodeClient = null;
	private Map<String,CacheListenerBridge<?, ?>> listenerMap = new Hashtable<>();

	
	protected GeodeClient(boolean cachingProxy, String... classPatterns)
	{
		 this(GeodeSettings.getInstance().getLocatorHost(),
		 	GeodeSettings.getInstance().getLocatorPort(),
		 	cachingProxy, 
		 	classPatterns);
	}//------------------------------------------------
	protected GeodeClient(String host, int port, boolean cachingProxy, String... classPatterns)
	{
		this.cachingProxy = cachingProxy;
			
		String name = Config.getProperty(GeodeConfigConstants.NAME_PROP,GeodeClient.class.getSimpleName());
		
		//check for exists client cache
		ClientCache cache = null;
		
		try
		{
			cache = ClientCacheFactory.getAnyInstance();	
		}
		catch(CacheClosedException e)
		{
			Debugger.println(e.getMessage());
		}
		
		try{
			if(cache != null)
				cache.close(); //close old connection
		}catch(Exception e)
		{Debugger.println(e.getMessage());}
		
		String className = Config.getProperty(
			GeodeConfigConstants.PDX_SERIALIZER_CLASS_NM_PROP,
			EnhancedReflectionSerializer.class.getName());
		
		PdxSerializer pdxSerializer = createPdxSerializer(className,classPatterns);
		
		Properties props = new Properties();
		try
		{
			constructSecurity(props);
		}
		catch(IOException e)
		{
			throw new ConfigException("Unable to configure security connection details ERROR:"+e.getMessage(),e);
		}
			
			this.clientCache = new ClientCacheFactory(props).addPoolLocator(host, port)
			.setPoolSubscriptionEnabled(true)
			.setPdxSerializer(pdxSerializer)
			.setPdxReadSerialized(GeodeConfigConstants.PDX_READ_SERIALIZED)
			.setPoolPRSingleHopEnabled(Config.getPropertyBoolean(
				GeodeConfigConstants.POOL_PR_SINGLE_HOP_ENABLED_PROP,true))
			.set("log-level", Config.getProperty("log-level","config"))
			.set("name", name)
			.create();
			
			//Caching Proxy
			cachingRegionfactory = clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);
			proxyRegionfactory = clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
		
	}//------------------------------------------------
	/**
	 * Initialize security properties
	 * @param props the security properties
	 * @throws IOException 
	 */
	protected static void constructSecurity(Properties props) throws IOException
	{
		props.setProperty("security-client-auth-init", GeodeConfigAuthInitialize.class.getName()+".create");
		
		//write to file
		File sslFile = saveEnvFile(GeodeConfigConstants.SSL_KEYSTORE_CLASSPATH_FILE_PROP);
		

		System.out.println("sslFile:"+sslFile);
		
		
		File sslTrustStoreFile = saveEnvFile(GeodeConfigConstants.SSL_TRUSTSTORE_CLASSPATH_FILE_PROP);
		String sslTrustStoreFilePath = "";
		if(sslTrustStoreFile != null)
			sslTrustStoreFilePath = sslTrustStoreFile.getAbsolutePath();
		
		props.setProperty("ssl-keystore",(sslFile != null) ?  sslFile.getAbsolutePath(): "");

		props.setProperty("ssl-keystore-password",Config.getPropertyEnv("ssl-keystore-password",""));
		
		props.setProperty("ssl-truststore",sslTrustStoreFilePath);
		props.setProperty("ssl-protocols",Config.getPropertyEnv("ssl-protocols",""));
		props.setProperty("ssl-truststore-password",Config.getPropertyEnv("ssl-truststore-password",""));
		props.setProperty("ssl-keystore-type",Config.getPropertyEnv("ssl-keystore-type","")   );
		props.setProperty("ssl-ciphers",Config.getPropertyEnv("ssl-ciphers",""));
		props.setProperty("ssl-require-authentication",Config.getPropertyEnv("ssl-require-authentication","")  );
		props.setProperty("ssl-enabled-components", Config.getPropertyEnv("ssl-enabled-components",""));
		
	}//------------------------------------------------
	/**
	 * 
	 * @param configPropFilePath the property name with the file path
	 * @return the saved File
	 * @throws IOException when there is an issue with saving the file
	 */
	private static File saveEnvFile(String configPropFilePath)
	throws IOException
	{
		String sslKeystorePath = Config.getProperty(configPropFilePath,"");
		
		String fileName = Paths.get(sslKeystorePath).toFile().getName();
		
		if(sslKeystorePath.length() == 0)
			return null;
		
		byte[] bytes = IO.readBinaryClassPath(sslKeystorePath);
		
		
		String sslDirectory = Config.getProperty(GeodeConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP,".");
		File sslDirectoryFile = Paths.get(sslDirectory).toFile();
		
		if(!sslDirectoryFile.exists())
		{
			throw new ConfigException("Configuration property "+GeodeConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP+" "+sslDirectoryFile+" but it does not exist");
		}
		else if(!sslDirectoryFile.isDirectory())
		{
			throw new ConfigException("Configuration property "+GeodeConfigConstants.SSL_KEYSTORE_STORE_DIR_PROP+" "+sslDirectoryFile+" but is not a valid directory");
		}

		File sslFile = Paths.get(sslDirectoryFile+IO.fileSperator()+fileName).toFile();
		
		IO.writeFile(sslFile, bytes);
		
		return sslFile;
	}//------------------------------------------------
	
	public <ReturnType> Collection<ReturnType>  select(String oql)
	{
		return select(oql,null);
	}//------------------------------------------------
	
	static PdxSerializer createPdxSerializer(String pdxSerializerClassNm, String... classPatterns )
	{
		
		Object [] initArgs = {classPatterns};
		return ClassPath.newInstance(pdxSerializerClassNm, initArgs);
	}
	//------------------------------------------------
	
	/**
	 * @param <K> the key class
	 * @param <V> the value class
	 * @param criteria the search criteria
	 * @param region the region
	 * @param filter the filter set
	 * @return collection of results
	 */
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public <K,V> Paging<V>  searchText(TextPageCriteria criteria, Region<K,V> region, Set<K> filter)
	{
		try
		{
			
			LuceneSearchFunction func = new LuceneSearchFunction();
			
			
			Execution<Object,Object,Object> exe = FunctionService.onRegion(region).withFilter(filter);
			
			Paging<V> paging = (Paging)GemFireIO.exeWithResults(exe, func);
			
			
			return paging;
		}
		catch (Exception e)
		{
			throw new SystemException(e);
		}		
	}
	
	
	public <ReturnType> Collection<ReturnType> select(String oql, RegionFunctionContext rfc)
	{
		return  Querier.query(oql, rfc);
	}//------------------------------------------------
	/**
	 * 
	 * @param clientCache the connection
	 */
	protected GeodeClient(ClientCache clientCache)
	{
		this(clientCache,
		clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY),
		clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU)
		);
	}//------------------------------------------------

	protected GeodeClient(ClientCache clientCache,  ClientRegionFactory<?, ?> proxyRegionfactory,
	 ClientRegionFactory<?, ?> cachingRegionfactory
	)
	{
		cachingProxy = false;
		this.clientCache = clientCache;
		this.proxyRegionfactory = proxyRegionfactory;
		this.cachingRegionfactory = cachingRegionfactory;
	}//------------------------------------------------
	/**
	 * 
	 * @param criteria the search criteria
	 * @return the collection keys in the page region
	 * @throws Exception when an unknow exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Collection<String> searchWithPageKeys(TextPageCriteria criteria)
	throws Exception
	{
		if(criteria == null)
			return null;
		
		Region<?,?> region = this.getRegion(criteria.getRegionName());
		
		Execution<?,?,?> exe = FunctionService.onRegion(region).setArguments(criteria);
		
		if(criteria.getFilter() != null)
		{
			exe = exe.withFilter(criteria.getFilter());
		}
		
	    return GemFireIO.exeWithResults(exe, new LuceneSearchFunction<Object>());
	    
	}//------------------------------------------------
	
	public <K,V> Map<K,V> readSearchResultsByPage(TextPageCriteria criteria, int pageNumber)
	{
		GeodeLuceneSearch search = new GeodeLuceneSearch(this.clientCache);
		
		Region<String,Collection<?>> pageRegion = this.getRegion(criteria.getPageRegionName());
		Region<K,V> region = this.getRegion(criteria.getRegionName());
		
		return search.readResultsByPage(criteria,pageNumber,region,pageRegion);
	}//------------------------------------------------
	public Collection<String> clearSearchResultsByPage(TextPageCriteria criteria)
	{
		GeodeLuceneSearch search = new GeodeLuceneSearch(this.clientCache);
	
		return search.clearSearchResultsByPage(criteria,this.getRegion(criteria.getPageRegionName()));
		
	}//------------------------------------------------
	/**
	 * 
	 * @return the querier service instance
	 */
	public QuerierService getQuerierService()
	{
		connect();
		return new QuerierMgr();
	}//------------------------------------------------
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <K,V> Region<K,V> createRegion(String regionName)
	{
		if(regionName.startsWith("/"))
			regionName = regionName.substring(1); //remove prefix
		
	
		
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		
		if(listener != null)
		{
			ClientRegionFactory<K, V> listenerRegionFactory = null;
			
			if(this.cachingProxy)
				listenerRegionFactory = this.clientCache.createClientRegionFactory(ClientRegionShortcut.CACHING_PROXY);	
			else
				listenerRegionFactory = this.clientCache.createClientRegionFactory(ClientRegionShortcut.PROXY);
			
			listenerRegionFactory.addCacheListener((CacheListener)listener);
			
			Region<K,V> region = listenerRegionFactory.create(regionName);
			
			region.registerInterestRegex(".*");
			return region;
			
		}
		
		if(this.cachingProxy)
			return (Region<K,V>)this.cachingRegionfactory.create(regionName);
		else
			return (Region<K,V>)this.proxyRegionfactory.create(regionName);
		
		
	}//------------------------------------------------
	/**
	 * This is an example to get or create a region
	 * @param regionName the  name
	 * @param <K> the key type
	 * @param <V> the value type
	 * @return the existing or created region (using the ClientbRegionFactory)
	 */
	@SuppressWarnings("unchecked")
	public <K,V> Region<K,V> getRegion(String regionName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return (Region<K,V>)region;
		
		region = (Region<K,V>)this.createRegion(regionName);
		
		//Client side data policy is typically NORMAL or EMPTY
		if(cachingProxy)
		{
			//NORMAL data policy are typically used for CACHING_PROXY
			//You should interest so updates for the server will be pushed to the clients
			region.registerInterestRegex(".*");
		}
		
		return region;
	}//------------------------------------------------
	/**
	 * Create a proxy region
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param clientCache the client cache
	 * @param regionName the region name
	 * @return the create region
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Region<K,V> getRegion(ClientCache clientCache, String regionName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return (Region<K,V>)region;
		
		region = (Region<K,V>)clientCache
		.createClientRegionFactory(ClientRegionShortcut.PROXY).create(regionName);
		
		return region;
	}//------------------------------------------------
	
	/**
	 * Create a proxy region
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param clientCache the client cache
	 * @param regionName the region name
	 * @param poolName the pool to use
	 * @return the create region
	 */
	@SuppressWarnings("unchecked")
	public static <K,V> Region<K,V> getRegion(ClientCache clientCache, String regionName, String poolName)
	{
		if(regionName == null || regionName.length() == 0)
			return null;
		
		Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
		
		if(region != null )
			return (Region<K,V>)region;
		
		region = (Region<K,V>)clientCache
		.createClientRegionFactory(ClientRegionShortcut.PROXY).setPoolName(poolName).create(regionName);
		
		return region;
	}//------------------------------------------------
	public <T> BlockingQueue<T> registerCq(String cqName,String oql) 
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
	
	
	
	/**
	 * 
	 * @return the GEODE client
	 */
	public synchronized static GeodeClient connect()
	{
		if(geodeClient != null)
			return geodeClient;
		
		boolean cachingProxy = Config.getPropertyBoolean(GeodeConfigConstants.USE_CACHING_PROXY_PROP,false).booleanValue();
		
		geodeClient = new GeodeClient(cachingProxy,
		Config.getProperty(GeodeConfigConstants.PDX_CLASS_PATTERN_PROP,".*"));
		
		return geodeClient;
	}//------------------------------------------------
	
	/**
	 * @return the clientCache
	 */
	public ClientCache getClientCache()
	{
		return clientCache;
	}
	/**
	 * @return the cachingProxy
	 */
	public boolean isCachingProxy()
	{
		return cachingProxy;
	}
	/**
	 * @param cachingProxy the cachingProxy to set
	 */
	public void setCachingProxy(boolean cachingProxy)
	{
		this.cachingProxy = cachingProxy;
	}//------------------------------------------------
	/**
	 * Add the observer as a listener for put/create events
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param regionName the region name
	 * @param consumer the observer
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <K,V> void  registerAfterPut(String regionName, Consumer<EntryEvent<K, V>> consumer)
	{
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		if(listener == null)
		{
			Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
			
			if(region != null )
				throw new IllegalStateException("Cannot register a listener when the region already created. Try registering the listener first. Then use GeodeClient.getRegion for regionName:"+regionName);
			
			listener = CacheListenerBridge.forAfterPut(consumer);
		}
		else
		{
			listener.addAfterPutListener(consumer);
		}
		
		this.listenerMap.put(regionName, listener);
		
	}//------------------------------------------------
	/**
	 * Add the observer as a listener for remove/invalidate events
	 * @param <K> the region key
	 * @param <V> the region value
	 * @param regionName the region name
	 * @param consumer the observer
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public   <K,V> void registerAfterDelete(String regionName, Consumer<EntryEvent<K,V>> consumer)
	{
		
		
		CacheListenerBridge<K, V> listener = (CacheListenerBridge)this.listenerMap.get(regionName);
		if(listener == null)
		{
			Region<K,V> region = (Region<K,V>)clientCache.getRegion(regionName);
			
			if(region != null )
				throw new IllegalStateException("Cannot register a listener when the region already created. Try registering the listener first. Then use GeodeClient.getRegion for regionName:"+regionName);
			
			listener = CacheListenerBridge.forAfterDelete(consumer);
		}
		else
		{
			listener.addAfterDeleteListener(consumer);
		}
		
	}
	

	
}

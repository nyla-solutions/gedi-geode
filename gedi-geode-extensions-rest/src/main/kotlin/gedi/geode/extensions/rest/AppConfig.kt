package gedi.geode.extensions.rest

import gedi.solutions.geode.client.GeodeClient
import gedi.solutions.geode.pcc.config.VCAPConfig
import org.apache.geode.cache.EvictionAttributes
import org.apache.geode.cache.client.ClientCache
import org.apache.geode.cache.client.ClientCacheFactory
import org.apache.geode.cache.client.ClientRegionFactory
import org.apache.geode.cache.client.ClientRegionShortcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.URI
import java.util.Properties
import gedi.solutions.geode.pcc.security.VCAPConfigAuthInitialize

/**
 * Configuration settings for the application
 */
@Configuration
class AppConfig
{
	/**
	 * GemFire connection object is a Client Cache
	 */
	@Autowired
	lateinit var clientCache : ClientCache; 

	/**
	 * Definite the GemFire connection cache
	 * @param environment the environment variables in application.yml, application.properties, system properties or evironment variables
	 * @return
	 */
	@Bean(name = arrayOf("client-cache"))
	fun getCache(@Autowired  environment : Environment) : ClientCache
	{
		var vcap : VCAPConfig = VCAPConfig.getInstance();
		
		var locatorList = vcap.getLocatorUrlList();
		var locatorURI : URI? = null;
		
		if(locatorList != null && !locatorList.isEmpty())
				locatorURI = locatorList.get(0);
		
		var port : Int = 0;
		
		var  host : String? = environment.getProperty("LOCATOR_HOST");
		
		if (host == null || host.trim().length == 0)
		{
			//check env
			if(locatorURI != null)
			{
				host = locatorURI.host;
				port = locatorURI.port;
			}
		}
		else
		{
			port = Integer.parseInt(environment.getProperty("LOCATOR_PORT"));
		}
			
		if (host == null || host.trim().length == 0)
			throw IllegalArgumentException("Environment property LOCATOR_HOST is required");
		
		var name : String? = environment.getProperty("name");
		
		if (name == null || name.trim().length == 0)
			throw IllegalArgumentException("Environment property name is required");
		
 		try
		{
			 var props : Properties = Properties();
 			 props.setProperty("security-client-auth-init", VCAPConfigAuthInitialize::class.java.name+".create");
			
			var factory : ClientCacheFactory = ClientCacheFactory(props)
			.addPoolLocator(host, port)
			.setPoolSubscriptionEnabled(true)
			.setPdxReadSerialized(true)
			.set("name", name);
			
			return factory.create();
		}
		catch ( e : NumberFormatException)
		{
			throw NumberFormatException("Environment variable LOCATOR_PORT is required");
		}
	}//------------------------------------------------
	/**
	 * Wire the example GemFire client wrapper
	 * @param clientCache the GemFire connection
	 * @param factory the factory to create regions
	 * @return 
	 */
	@Bean
	fun getGeode( clientCache : ClientCache,  factory : ClientRegionFactory<Any, Any>) : GeodeClient
	{
		return GeodeClient(clientCache, factory);
	}//------------------------------------------------
	
	/**
	 * GemFire factory to create regions
	 * @return the factory with the ClientRegionShortcut.PROXY
	 */
	@Bean
	 @SafeVarargs
	fun <K,V> getProxyClientRegionFactory() : ClientRegionFactory<K,V> 
	{
		// The options are PROX or Caching Proxy
		//Caching PROXY has a better response time performance
		//but requires you register interest (set getRegion)
		 var factory : ClientRegionFactory<K,V> = clientCache
		.createClientRegionFactory<K,V>(ClientRegionShortcut.CACHING_PROXY_HEAP_LRU)
		.setEvictionAttributes(EvictionAttributes.createLRUHeapAttributes());
		
		
		return factory;
	}//------------------------------------------------	

}
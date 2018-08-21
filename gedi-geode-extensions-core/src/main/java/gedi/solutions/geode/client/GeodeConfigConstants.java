package gedi.solutions.geode.client;

import org.apache.geode.pdx.ReflectionBasedAutoSerializer;

import nyla.solutions.core.util.Config;

public interface GeodeConfigConstants
{
	
	public final static String PDX_SERIALIZER_CLASS_NM = Config.getProperty("PDX_SERIALIZER_CLASS_NM",ReflectionBasedAutoSerializer.class.getName()); 
	
	public final static String LOCATOR_HOST_PROP = "LOCATOR_HOST";
	public final static String LOCATOR_PORT_PROP = "LOCATOR_PORT";
	

	public final static String USER_NAME = "security-username";
	public final static String PASSWORD = "security-password";
	public final static String TOKEN = "security-token";
	
	/**
	 * PARTITION SINGLE HOP ENABLED (default false)
	 */
	public final static boolean POOL_PR_SINGLE_HOP_ENABLED = Config.getPropertyBoolean("POOL_PR_SINGLE_HOP_ENABLED",true).booleanValue();
	
	
	public final static String PDX_CLASS_PATTERN = Config.getProperty("PDX_CLASS_PATTERN",".*");
	
	public final static String USE_CACHING_PROXY_PROP = "USE_CACHING_PROXY";

	/**
	 * PDX_READ_SERIALIZED default false
	 */
	public final static  boolean PDX_READ_SERIALIZED = Config.getPropertyBoolean("PDX_READ_SERIALIZED",false);
	 
}

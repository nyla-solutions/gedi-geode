package gedi.solutions.geode.client;

import nyla.solutions.core.util.Config;

public interface GeodeConfigConstants
{
	public final String LOCATOR_HOST_PROP = "LOCATOR_HOST";
	public final String LOCATOR_PORT_PROP = "LOCATOR_PORT";
	

	public final String USER_NAME = "security-username";
	public final String PASSWORD = "security-password";
	public final String TOKEN = "security-token";
	
	public final String PDX_CLASS_PATTERN_PROP = "PDX_CLASS_PATTERN";
	
	
	public final String USE_CACHING_PROXY_PROP = "USE_CACHING_PROXY";


	public static boolean PDX_READ_SERIALIZED = Config.getPropertyBoolean("PDX_READ_SERIALIZED",false);
	 
}

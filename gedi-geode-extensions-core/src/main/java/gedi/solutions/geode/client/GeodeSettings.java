package gedi.solutions.geode.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;

import gedi.solutions.geode.security.UserSecuredCredentials;
import nyla.solutions.core.exception.ConfigException;
import nyla.solutions.core.security.SecuredToken;
import nyla.solutions.core.util.Config;

/**
 * <pre>
 * VCAP is an environment variable used by cloudfoundry to pass configuration information
 *
 *{
  "p-cloudcache": [
    {
      "credentials": {
    "locators": [
      "10.244.0.4[55221]",
      "10.244.1.2[55221]",
      "10.244.0.130[55221]"
    ],
    "urls": {
      "gfsh": "http://cloudcache-5574c540-1464-4f9e-b56b-74d8387cb50d.local.pcfdev.io/gemfire/v1",
      "pulse": "http://cloudcache-5574c540-1464-4f9e-b56b-74d8387cb50d.local.pcfdev.io/pulse"
    },
    "users": [
      {
        "password": "some_developer_password",
        "username": "developer"
      },
      {
        "password": "some_password",
        "username": "cluster_operator"
      }
    ]
      },
      "label": "p-cloudcache",
      "name": "test-service",
      "plan": "caching-small",
      "provider": null,
      "syslog_drain_url": null,
      "tags": [],
      "volume_mounts": []
    }
  ]
 * </pre>
 * 
 * }
 */
public class GeodeSettings
{
	/**
	 * VCAP_SERVICES = "VCAP_SERVICES"
	 */
	public static final String VCAP_SERVICES = "VCAP_SERVICES";

	private static GeodeSettings instance = null;
	private final String envContent;
	private final Pattern p = Pattern.compile("(.*)\\[(\\d*)\\]");
	private final String locatorHost;
	private final int locatorPort;

	/**
	 * Get from VCAP_SERVICES system environment variable or JVM system property
	 */
	private GeodeSettings()
	{

	this(System.getenv().get(VCAP_SERVICES) !=null 
		? System.getenv().get(VCAP_SERVICES) 
		: System.getProperty(VCAP_SERVICES));
		
	}// ------------------------------------------------

	GeodeSettings(String envContent)
	{
		this.envContent = envContent;
		
		List<URI> locatorList = getLocatorUrlList();
		URI locatorURI = null;
		
		if(locatorList != null && !locatorList.isEmpty())
				locatorURI = locatorList.get(0);
		
		int port = 10334;
		
		String host = Config.getProperty(GeodeConfigConstants.LOCATOR_HOST_PROP,"");
		
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
			port = Config.getPropertyInteger(GeodeConfigConstants.LOCATOR_PORT_PROP,10334).intValue();
			
			if(host.trim().length() == 0)
				throw new ConfigException(GeodeConfigConstants.LOCATOR_PORT_PROP+" configuration property required");
		}
		
		this.locatorHost = host;
		this.locatorPort = port;
	}// ------------------------------------------------

	/**
	 * @return the locatorHost
	 */
	public String getLocatorHost()
	{
		return locatorHost;
	}

	/**
	 * 
	 * @return the locator port
	 */
	public int getLocatorPort()
	{
		return locatorPort;
	}//------------------------------------------------

	/**
	 * 
	 * @return the VCAPConfig
	 */
	public static GeodeSettings getInstance()
	{
		synchronized (GeodeSettings.class)
		{
			if (instance == null)
			{
				instance = new GeodeSettings();
			}
		}
		return instance;
	}// ------------------------------------------------
	
	

	@SuppressWarnings("unchecked")
	public String getLocators()
	{
		StringBuilder locatorList = new StringBuilder();
		Map<String, ?> credentials = getCredentials();

		if (credentials == null)
			return null;

		List<String> locators = (List<String>) credentials.get("locators");
		for (String locator : locators)
		{

			if (locatorList.length() != 0)
				locatorList.append(",");

			locatorList.append(locator);
		}
		return locatorList.toString();
	}// ------------------------------------------------

	@SuppressWarnings("unchecked")
	public List<URI> getLocatorUrlList() 

	{
		List<URI> locatorList = new ArrayList<URI>();
		Map<String, ?> credentials = getCredentials();

		if (credentials == null)
			return null;

		List<String> locators = (List<String>) credentials.get("locators");
		try
		{
			for (String locator : locators)
			{
				Matcher m = p.matcher(locator);
				if (!m.matches())
				{
					throw new IllegalStateException("Unexpected locator format. expected host[port], got" + locator);
				}
				locatorList.add(new URI("locator://" + m.group(1) + ":" + m.group(2)));
			}
			return locatorList;
		}
		catch (URISyntaxException e)
		{
			throw new ConfigException("One of the provided locators has an incorrect syntax:"+locatorList);
		}
	}// ------------------------------------------------

	
	/**
	 * 
	 * @return the secured token information
	 */
	public SecuredToken getSecuredToken()
	{
		return getSecuredToken(null, null);
	}// ------------------------------------------------

	/**
	 * 
	 * @param token
	 *            the token to initialize
	 * @return the secured token information
	 */
	public SecuredToken getSecuredToken(String token)
	{
		return getSecuredToken(null, token);
	}// ------------------------------------------------

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public SecuredToken getSecuredToken(String username, String token)
	{

		Map<String, ?> credentials = getCredentials();

		if (credentials == null)
			return null;

		List<Map<String, String>> users = (List) credentials.get("users");

		if (users == null)
			return null;

		Map<String, String> map = null;

		if (username == null || username.trim().length() == 0)
		{
			map = users.iterator().next();
		}
		else
		{
			map = users.stream()
			.filter(m -> username.equals(m.get("username")))
			.findFirst().orElse(null);
		}

		if (map == null)
			return null;

		String password = map.get("password");
		if (password == null)
			password = "";

		return new UserSecuredCredentials(map.get("username"), password.toCharArray(), token);
	}// ------------------------------------------------

	/**
	 * 
	 * @return the map with credentials
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Map<String, ?> getCredentials()
	{

		if (envContent == null || envContent.trim().length() == 0)
			return null;

		Map<String, ?> credentials = null;

		try
		{
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, ?> services = objectMapper.readValue(envContent, Map.class);
			List<Map<String, ?>> gemfireService = getGemFireService(services);
			if (gemfireService != null)
			{
				Map<String, ?> serviceInstance = (Map) gemfireService.get(0);
				credentials = (Map<String, ?>) serviceInstance.get("credentials");
			}

			return credentials;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}// ------------------------------------------------

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List<Map<String, ?>> getGemFireService(Map services)
	{
		List<Map<String, ?>> l = (List) services.get("p-cloudcache");
		if (l == null)
		{
			throw new IllegalStateException("cloud cache service is not bound to this application");
		}
		return l;
	}

}

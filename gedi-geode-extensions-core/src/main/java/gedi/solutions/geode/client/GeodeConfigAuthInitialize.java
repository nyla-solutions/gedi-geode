package gedi.solutions.geode.client;

import java.util.Properties;
import org.apache.geode.LogWriter;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.security.AuthInitialize;
import org.apache.geode.security.AuthenticationFailedException;

import nyla.solutions.core.security.SecuredToken;
import nyla.solutions.core.util.Config;

/**
 * <pre>
 * Set ENVIRONMENT
 * 
 * security-username
 * 
 * USER_NAME);
	    String token = Config.getProperty(TOKEN,"")
	    
   </pre>
 * @author Gregory Green
 *
 */
public class GeodeConfigAuthInitialize
implements AuthInitialize 
{
	private final GeodeSettings vcapConfig;

	  public static final String USER_NAME = "security-username";
	  public static final String PASSWORD = "security-password";
	  public static final String TOKEN = "security-token";
	  
	  //private LogWriter logWriter, securityLogWriter;

	  /**
	   * Constructor
	   * @param vcapConfig the VCAP configuration
	   */
	  protected GeodeConfigAuthInitialize(GeodeSettings vcapConfig)
	  {
		  this.vcapConfig = vcapConfig;
	  }//------------------------------------------------
	  public static AuthInitialize create() {
	    return new GeodeConfigAuthInitialize(GeodeSettings.getInstance());
	  }

	  @Override
	  public void close() {
	  }

	  @Override
	  public Properties getCredentials(Properties inputProperties, DistributedMember distributedMember,
	                                   boolean arg2) throws AuthenticationFailedException 
	  {

	    Properties props = new Properties();
	    String username = Config.getProperty(USER_NAME,"");
	    String password = Config.getProperty(PASSWORD,"");
	    String token = Config.getProperty(TOKEN,"");
	    
	    
	     SecuredToken securedToken = vcapConfig.getSecuredToken(username, token);
	     	     
	     if(securedToken == null)
	     {
	    	 props.setProperty(USER_NAME, username);
	    	 props.setProperty(PASSWORD, password);
	    	 return props;
	     }
	     
	     System.out.println("ISSUE-SECURED_TOKEN _NAME"+securedToken.getName());
	     
	      password = String.valueOf(securedToken.getCredentials());
	      props.setProperty(USER_NAME, securedToken.getName());
	      props.setProperty(PASSWORD, password);
	      props.setProperty(TOKEN, token);
	      
	    return props;
	  }

	  @Override
	  public void init(LogWriter logWriter, LogWriter securityLogWriter)
	      throws AuthenticationFailedException 
	  {
		  //this.logWriter = logWriter;
		  //this.securityLogWriter = securityLogWriter;
	  }
}
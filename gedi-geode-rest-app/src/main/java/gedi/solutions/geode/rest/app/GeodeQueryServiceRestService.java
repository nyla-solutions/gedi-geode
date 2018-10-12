package gedi.solutions.geode.rest.app;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gedi.solutions.geode.io.QuerierService;
import gedi.solutions.geode.rest.app.exception.DataError;
import gedi.solutions.geode.rest.app.exception.FaultAgent;

/**
 * Generic READ/WRITE GemFire REST service
 * @author Gregory Green
 *
 */
@RestController
@RequestMapping("/query")
public class GeodeQueryServiceRestService
{
	@Autowired
	QuerierService querierService;
 
	@Autowired
	FaultAgent faultAgent;
	
	/**
	 * Execute a GemFire query with no limit
	 * @param query the OQL to execute
	 * @return the JSON array records
	 * @throws Exception
	 */
	@PostMapping(path = "/",produces = "application/json")
	public String query(@RequestBody String query)
	throws Exception
	{
		return queryLimit(query, -1);
	}//------------------------------------------------
	/**
	 * Execute a GemFire query
	 * @param query the OQL to execute
	 * @param limit the results count limit
	 * @return the JSON records
	 * @throws Exception
	 */
	@PostMapping(path = "{limit}",produces = "application/json")
	public String queryLimit(@RequestBody String query, @PathVariable int limit)
	throws Exception
	{
		if(query == null || query.length() ==0)
			return null;
		
		  try {

			  query = this.appendLimit(query, limit);
			  
			  Logger logger = LogManager.getLogger(getClass());
			  logger.info("QueryService: START query "+query);
	            Collection<Object> results = querierService.query(query);
	            logger.info("QueryService: END query "+query);

	            if(results == null)
	                return null;

	            StringBuilder responseJson = new StringBuilder();

	           //TODO: concerns on performance
	           Boolean isPdx = null;
	           
	       
	            for (Object obj : results) {
	                	
	                	if(isPdx == null)
	                		isPdx = Boolean.valueOf(obj instanceof PdxInstance);

	                	if( responseJson.length() > 0)
	                	{
	                		responseJson.append(",");
	                	}
	                	
	                	if(Boolean.TRUE.equals(isPdx))
	                	{
	                		responseJson.append(JSONFormatter.toJSON((PdxInstance)obj));	
	                	}
	                	else
	                	{
	                		//escape quote to \\
	                		responseJson.append("\"").append(String.valueOf(obj)
	                		.replace("\\", "\\\\")
	                		.replace("\"", "\\\"")).append("\"");	
	                	}
	            }
	            
	            StringBuilder allResults = new StringBuilder().append("[").append(responseJson).append("]");

	            return allResults.toString();
	        }
	        catch(Exception e)
	        {
	            e.printStackTrace();
	            throw e;
	        }
	}//------------------------------------------------
	/**
     * Handling exceptions in general for REST responses
     * @param request the HTTP request
     * @param response the HTTP reponse
     * @param e the exception
     * @return Data Error details
     */
    @ExceptionHandler(Exception.class)
    DataError  handleException(HttpServletRequest request, HttpServletResponse response,Exception e ) 
    {
        return faultAgent.handleException(request,response,e);
    }//------------------------------------------------
	/**
	 * 
	 * @param query the OQL to append
	 * @param limit the limit number
	 * @return the OQL appended with limit if > 0
	 */
	protected String appendLimit(String query, int limit)
	{
		if(limit <= 0 || query.contains(" limit "))
			return query;
		
		return new StringBuilder(query).append(" limit ").append(limit).toString();
	}

	
}

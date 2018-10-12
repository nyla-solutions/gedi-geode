package gedi.solutions.geode.rest.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.client.ServerOperationException;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gedi.solutions.geode.client.GeodeClient;
import gedi.solutions.geode.rest.app.exception.DataError;
import gedi.solutions.geode.rest.app.exception.DataServiceSystemException;
import gedi.solutions.geode.rest.app.exception.FaultAgent;

/**
 * Service for manage region records
 */
@RestController
@RequestMapping("/region")
public class GeodeRegionRestService
{
	@Autowired
	GeodeClient geode;
	
	@Autowired
	FaultAgent faultAgent;

	
	/**
	 * Put a new records
	 * @param region the region name 
	 * @param key the region key
	 * @param value the region value
	 * @return previous Region values in JSON
	 */
	@PostMapping(path = "{region}/{key}",produces = "application/json")
	public String putEntry(@PathVariable String region ,@PathVariable String key, @RequestBody String value) 
	{
		
		if(region == null || region.length() == 0 || key == null || value ==null)
			return null;

		try {
			Region<String, PdxInstance> gemRegion  = geode.getRegion(region);

            System.out.println("Putting key $key in region $region");
            
            if(!value.contains("@type"))
            {
            	throw new IllegalArgumentException("@type required in JSON value. Example JSON string: {\n" + 
            	"    \"@type\": \"package.ObjectName\",\n" + 
            	"     \"field1\": 1121,\n" + 
            	"     \"field2\": 1012,\n" + 
            	"     \"field3\":  \"example\"}");
            }
            
			PdxInstance pdxInstance = JSONFormatter.fromJSON(value);

			PdxInstance response = gemRegion.put(key, pdxInstance);

			if (response == null)
				return null;

			return JSONFormatter.toJSON(response);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw e;
		}
	}//------------------------------------------------
	
	/**
	 * Delete a region entry by a key
	 * @param regionName the region name
	 * @param key the region key
	 * @return the deleted region value in JSON format
	 */
	@DeleteMapping(path = "{regionName}/{key}",produces = "application/json")
	public String delete(@PathVariable String regionName,@PathVariable  String key)
	{
		Region<String,PdxInstance> region   = this.geode.getRegion(regionName);
		
		PdxInstance pdx = region.remove(key);
		
		if(pdx == null)
			return null;
		
		return JSONFormatter.toJSON(pdx);
		
	}//------------------------------------------------
	/**
	 * Get a value by a given key
	 * @param region the region name
	 * @param key the region key
	 * @return the value of the region in JSON format
	 */
	@GetMapping(path = "{region}/{key}",produces = "application/json")
	String getValueByKey(@PathVariable String region,@PathVariable String  key)
	{
		try
		{
			if(region == null || region.length() == 0 || key == null)
				return null;
			
			Region<String, Object> gemRegion = geode.getRegion(region);
			
			Object value = gemRegion.get(key);
			
			if(value == null)
				return null;
			
			
			if(value instanceof PdxInstance)
				return JSONFormatter.toJSON((PdxInstance)value);
			return value.toString();
		}
		catch(ServerOperationException serverError)
		{
			Throwable cause = serverError.getRootCause();
			if(cause instanceof RegionDestroyedException)
			{
				throw new DataServiceSystemException("Region \""+region+"\" not found");
			}
			throw new DataServiceSystemException(serverError.getMessage(),serverError);
		}
		catch (RuntimeException e)
		{
		
			e.printStackTrace();
			
			throw new DataServiceSystemException(e.getMessage(),e);
		}
	}//------------------------------------------------
	/**
	 * HAndling exceptions in general for REST responses
	 * @param request the HTTP request
	 * @param response the HTTP reponse
	 * @param e the exception
	 * @return Data Error details
	 */
	/**
     * Handling exceptions in general for REST responses
     * @param request the HTTP request
     * @param response the HTTP reponse
     * @param e the exception
     * @return Data Error details
     */
    @ExceptionHandler(Exception.class)
    private DataError  handleException(HttpServletRequest request, HttpServletResponse response,Exception e ) 
    {
        return faultAgent.handleException(request,response,e);
    }//------------------------------------------------
}

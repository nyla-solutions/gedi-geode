package gedi.geode.extensions.rest

import gedi.geode.extensions.rest.exception.DataError
import gedi.solutions.geode.client.GeodeClient
import org.apache.geode.cache.Region
import org.apache.geode.cache.RegionDestroyedException
import org.apache.geode.cache.client.ServerOperationException
import org.apache.geode.pdx.JSONFormatter
import org.apache.geode.pdx.PdxInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import nyla.solutions.core.util.Debugger


/**
 * Service for manage region records
 */
@RestController
@RequestMapping("/region")
open class GeodeRegionService
{
	@Autowired
	lateinit var geode : GeodeClient
	
	/**
	 * Put a new records
	 * @param region the region name 
	 * @param key the region key
	 * @param value the region value
	 * @return previous Region values in JSON
	 */
	@PostMapping(path = arrayOf("{region}/{key}"),produces = arrayOf("application/json"))
	fun putEntry(@PathVariable region : String?,@PathVariable key : String?, @RequestBody value: String?) : String?
	{
		
		if(region == null || region.length == 0 || key == null)
			return null;

		try {
			//Region<String, PdxInstance> gemRegion = geode.getRegion(region);
			var gemRegion: Region<String, PdxInstance> = geode.getRegion(region)

            System.out.println("Putting key $key in region $region")
            
			var pdxInstance = JSONFormatter.fromJSON(value);

			var response = gemRegion.put(key, pdxInstance);

			if (response == null)
				return null;

			return JSONFormatter.toJSON(response);
		}
		catch(e: Exception )
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
	@DeleteMapping(path = arrayOf("{regionName}/{key}"),produces = arrayOf("application/json"))
	fun delete(@PathVariable regionName : String,@PathVariable   key : String): String?
	{
		var region : Region<String,PdxInstance>  = this.geode.getRegion(regionName);
		
		var pdx : PdxInstance? = region.remove(key);
		
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
	@GetMapping(path = arrayOf("{region}/{key}"),produces = arrayOf("application/json"))
	fun getValueByKey(@PathVariable  region : String?,@PathVariable  key : String?) : String?
	{
		try
		{
			if(region == null || region.length == 0 || key == null)
				return null;
			
			var gemRegion : Region<String, Any> = geode.getRegion(region);
			
			var value = gemRegion.get(key);
			
			if(value == null)
				return null;
			
			
			if(value is PdxInstance)
				return JSONFormatter.toJSON(value);
			return value as String;
		}
		catch( serverError : ServerOperationException)
		{
			var cause : Throwable? = serverError.getRootCause();
			if(cause is RegionDestroyedException)
			{
				throw DataServiceSystemException("Region \""+region+"\" not found");
			}
			throw DataServiceSystemException(serverError.message,serverError);
		}
		catch (e : RuntimeException)
		{
		
			e.printStackTrace();
			
			throw DataServiceSystemException(e.message,e);
		}
	}//------------------------------------------------
	/**
	 * HAndling exceptions in general for REST responses
	 * @param request the HTTP request
	 * @param response the HTTP reponse
	 * @param e the exception
	 * @return Data Error details
	 */
	@ExceptionHandler(Exception::class)
	fun  handleException(request : HttpServletRequest,  response : HttpServletResponse,  e : Exception) : DataError 
	{
		
		val cause = e.cause;
		
		var dataError = DataError();
		dataError.path = request.getRequestURI();
		
		dataError.error = "Processing error";
		
		if(e is ServerOperationException)
		{
			dataError.error ="Server opertion error";
			
			if(cause is RegionDestroyedException)
			{
				var regionDestroyedException = cause;
				dataError.message = "Region region:"+regionDestroyedException.getRegionFullPath()+" not found";
			}
		}
		response.status = 500;
		
		dataError.message = e.message;
		dataError.stackTrace = Debugger.stackTrace(e);
		
		return dataError;
	}//------------------------------------------------
}
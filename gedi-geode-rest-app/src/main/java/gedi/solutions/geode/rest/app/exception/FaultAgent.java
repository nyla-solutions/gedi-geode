package gedi.solutions.geode.rest.app.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.cache.client.ServerOperationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import nyla.solutions.core.util.Debugger;

@Component
public class FaultAgent
{

	@ExceptionHandler
	 public DataError handleException(HttpServletRequest request, HttpServletResponse response,Exception e ) 
	 {

	    	DataError dataError = new DataError();
	    	
	    	dataError.setError("Processing error");
	    	if(e != null)
	    	{
	    		Throwable cause = e.getCause();

	    		dataError.setMessage(e.getMessage());
	    		
	            if(e instanceof ServerOperationException)
	            {
	                dataError.setError("Server opertion error");

	                if(cause instanceof RegionDestroyedException)
	                {
	                	RegionDestroyedException regionDestroyedException = (RegionDestroyedException)cause;
	                    dataError.setMessage("Region region:"+regionDestroyedException.getRegionFullPath()+" not found");
	                }
	            }
	            
	    	}
	    	
	        dataError.setPath(request.getRequestURI());
	        dataError.setStackTrace(Debugger.stackTrace(e));
	        response.setStatus(500);
	        
	        

	        return dataError;
	    }//------------------------------------------------
}

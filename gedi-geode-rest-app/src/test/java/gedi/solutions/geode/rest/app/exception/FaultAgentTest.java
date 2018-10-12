package gedi.solutions.geode.rest.app.exception;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class FaultAgentTest
{

	/**
	 * Test handle exception
	 */
	@Test
	public void testHandleException()
	{
		FaultAgent fa = new FaultAgent();
		
		HttpServletRequest request = mock(HttpServletRequest.class);
		HttpServletResponse response = mock(HttpServletResponse.class);
	
		DataError de = fa.handleException(request, response, null);
		
		assertNotNull(de);
		
		
	}//------------------------------------------------

}

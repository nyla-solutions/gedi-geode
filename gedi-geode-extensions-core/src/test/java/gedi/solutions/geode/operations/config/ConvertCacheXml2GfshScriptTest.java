package gedi.solutions.geode.operations.config;

import static org.junit.Assert.*;

import org.junit.Test;

public class ConvertCacheXml2GfshScriptTest
{

	@Test
	public void testNoArgs() throws Exception
	{
		String[] args = {};
		
		try 
		{
			ConvertCacheXml2GfshScript.main(args);
			fail();
		}
		catch(IllegalArgumentException e)
		{
			
		}
	}
	
	@Test
	public void testMain()
	{
		String[] args = {"src/test/resources/xml/clusterCopy.xml"};
		
		ConvertCacheXml2GfshScript.main(args);
	}

}

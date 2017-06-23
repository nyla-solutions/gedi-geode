package gedi.solutions.geode.operations.config;

import org.junit.Test;

public class ConvertCacheXml2GfshScriptTest
{

	@Test
	public void testMain()
	{
		String[] args = {"src/test/resources/xml/clusterCopy.xml"};
		
		ConvertCacheXml2GfshScript.main(args);
	}

}

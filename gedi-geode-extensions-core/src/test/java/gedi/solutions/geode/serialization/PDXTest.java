package gedi.solutions.geode.serialization;

import static org.junit.Assert.*;

import org.apache.geode.pdx.PdxInstance;
import org.junit.Test;

import gedi.solutions.geode.client.GeodeClient;
import nyla.solutions.core.io.IO;

public class PDXTest
{

	@Test
	public void testFromJSON()
	throws Exception
	{
		GeodeClient.connect();
		
		String json = IO.readFile("src/test/resources/json/object.json");
		PdxInstance pdx = PDX.fromJSON(json);
		
		assertNotNull(pdx);
		
		
	}

}

package gedi.solutions.geode.serialization;

import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;

public class PDX
{
	public static PdxInstance fromJSON(String json)
	{
		
		return JSONFormatter.fromJSON(json);
	}
}

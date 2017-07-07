package gedi.solutions.geode.functions;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;

public interface ExecutionFactory
{
	
	Execution onRegion(Region<?,?> region);

}

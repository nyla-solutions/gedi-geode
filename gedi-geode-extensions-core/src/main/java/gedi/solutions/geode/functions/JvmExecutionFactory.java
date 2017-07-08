package gedi.solutions.geode.functions;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;

/**
 * @author Gregory Green
 *
 */
public class JvmExecutionFactory implements ExecutionFactory
{

	@SuppressWarnings("rawtypes")
	@Override
	public Execution onRegion(Region<?, ?> region)
	{
		
		JvmExecution exe = new JvmExecution<>(region);
		
		return exe;
	}

}

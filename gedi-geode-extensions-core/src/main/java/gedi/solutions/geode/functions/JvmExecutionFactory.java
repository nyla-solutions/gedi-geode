package gedi.solutions.geode.functions;


import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;

/**
 * @author Gregory Green
 *
 */
public class JvmExecutionFactory implements ExecutionFactory
{

	/* (non-Javadoc)
	 * @see com.jnj.lpfg.lpfg_functions.io.ExecutionFactory#onRegion(com.gemstone.gemfire.cache.Region)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public Execution onRegion(Region<?, ?> region)
	{
		
		JvmExecution exe = new JvmExecution<>(region);
		
		return exe;
	}

}

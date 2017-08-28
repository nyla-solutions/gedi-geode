package gedi.solutions.geode.functions;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;

/**
 * Supports executing functions locally within the JVM
 * 
 * @author Gregory Green
 *
 */
public class JvmExecution extends ExecutionAdapter
{
	
	public JvmExecution(Region<?,?> region)
	{
		if (region == null)
			throw new IllegalArgumentException("region: required");
		this.dataSet = region;
	}//-------------------------------------------------------------------
	
	@Override
	public Execution withFilter(Set<?> filter)
	{
		this.filter = filter;
		return this;
	}

	@Override
	public Execution withCollector(ResultCollector<?,?> arg0)
	{
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	public Execution withArgs(Object arg0)
	{
		this.arguments = arg0;
		return this;
	}


	public Execution setArguments(Object args)
	{
		 this.arguments = args;
		 return this;
	}
	/**
	 * 
	 * @param function the function to exe
	 * @return the result collector
	 * @throws FunctionException when the server side exception occurs
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResultCollector<?, ?> execute(Function function) throws FunctionException
	{
		JvmResultsSender resultSender = new JvmResultsSender();
		JvmResultCollector jmvResultCollector = new JvmResultCollector(resultSender);
		
		JvmRegionFunctionContext<?,?, ?> rfc = new JvmRegionFunctionContext
				(dataSet, resultSender, arguments, filter);
		
		function.execute(rfc);
		
		
		return jmvResultCollector;
	}//-------------------------------------------------------------------

	private final Region<?,?> dataSet;
	private Set<?> filter = null;
	
	private Object arguments = null;
}

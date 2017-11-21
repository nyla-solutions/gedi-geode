package gedi.solutions.geode.functions;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;

import nyla.solutions.core.exception.NotImplementedException;

/**
 * Supports executing functions locally within the JVM
 * 
 * @author Gregory Green
 * @param <IN> the input object for execution
 * @param <OUT> the output object for execution 
 * @param <AGG> the aggregation
 *
 */
public class JvmExecution<IN, OUT, AGG> implements Execution<IN, OUT, AGG>
{
	
	public JvmExecution(Region<?,?> region)
	{
		if (region == null)
			throw new IllegalArgumentException("region: required");
		
		
		this.dataSet = region;
	}//-------------------------------------------------------------------
	
	@Override
	public ResultCollector<OUT, AGG> execute(String functionId) throws FunctionException
	{
		throw new NotImplementedException();
	}
	
	@Override
	public Execution<IN, OUT, AGG> withFilter(Set<?> filter)
	{
		this.filter = filter;
		return this;
	}
	
	@Override
	public Execution<IN, OUT, AGG> withCollector(ResultCollector<OUT, AGG> resultcollector)
	{
		throw new NotImplementedException();
	}

	@Override
	public Execution<IN, OUT, AGG> withArgs(Object args)
	{
		return this.setArguments(args);
	}


	public Execution<IN, OUT, AGG> setArguments(Object args)
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
	public ResultCollector<OUT, AGG> execute(Function function) throws FunctionException
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

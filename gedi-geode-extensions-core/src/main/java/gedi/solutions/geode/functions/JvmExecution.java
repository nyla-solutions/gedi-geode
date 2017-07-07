package gedi.solutions.geode.functions;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;

public class JvmExecution<K,V,T> extends ExecutionAdapter
{
	
	public JvmExecution(Region<K,V> region)
	{
		if (region == null)
			throw new IllegalArgumentException("region: required");
		this.dataSet = region;
	}//-------------------------------------------------------------------
	public Execution withFilter(Set<?> filter)
	{
		this.filter = filter;

		return this;
	}//-------------------------------------------------------------------

	public Execution withArgs(Object args)
	{
		this.arguments = args;
		
		return this;
	}//-------------------------------------------------------------------

	@SuppressWarnings("rawtypes")
	public Execution withCollector(ResultCollector resultCollector)
	{
		throw new RuntimeException("Not implemented");
	}//-------------------------------------------------------------------
	/**
	 * 
	 * @param function the function to exe
	 * @return the result collector
	 * @throws FunctionException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ResultCollector<?, ?> execute(Function function) throws FunctionException
	{
//		if(LeapFrogReadOnlyFunction.class.isAssignableFrom(function.getClass()))
//		{
//			LeapFrogReadOnlyFunction<?> leapFrogReadOnlyFunction =  (LeapFrogReadOnlyFunction)function;
//			leapFrogReadOnlyFunction.setRegionDictionary(new ClientRegionDictionary());
//			
//			leapFrogReadOnlyFunction.setExecutionFactory(new JvmExecutionFactory());
//		}
		
		JvmResultsSender resultSender = new JvmResultsSender();
		JvmResultCollector jmvResultCollector = new JvmResultCollector(resultSender);
		
		JvmRegionFunctionContext<K, V, T> rfc = new JvmRegionFunctionContext
				(dataSet, resultSender, arguments, filter);
		
		function.execute(rfc);
		
		
		return jmvResultCollector;
	}//-------------------------------------------------------------------

	private final Region<K,V> dataSet;
	private Set<?> filter = null;
	
	private Object arguments = null;
}

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
 * @param <K> the key type
 * @param <V> the value value
 * @param <T> the generic type
 */
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

	/**
	 *
	 * @param resultCollector the result collector
	 * @return the execution with the set result collector
	 */
	public Execution withCollector(@SuppressWarnings("rawtypes") ResultCollector resultCollector)
	{
		throw new RuntimeException("Not implemented");
	}//-------------------------------------------------------------------
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
		
		JvmRegionFunctionContext<K, V, T> rfc = new JvmRegionFunctionContext
				(dataSet, resultSender, arguments, filter);
		
		function.execute(rfc);
		
		
		return jmvResultCollector;
	}//-------------------------------------------------------------------

	private final Region<K,V> dataSet;
	private Set<?> filter = null;
	
	private Object arguments = null;
}

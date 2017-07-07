package gedi.solutions.geode.functions;


import java.util.Set;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;

/**
 * @author ggreen11
 *
 */
public abstract class ExecutionAdapter implements Execution
{

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#withFilter(java.util.Set)
	 */
	public Execution withFilter(Set<?> paramSet)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#withArgs(java.lang.Object)
	 */
	public Execution withArgs(Object paramObject)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#withCollector(com.gemstone.gemfire.cache.execute.ResultCollector)
	 */
	public Execution withCollector(ResultCollector<?, ?> paramResultCollector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#execute(java.lang.String)
	 */
	public ResultCollector<?, ?> execute(String paramString) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#execute(com.gemstone.gemfire.cache.execute.Function)
	 */
	public ResultCollector<?, ?> execute(Function paramFunction) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#execute(java.lang.String, boolean)
	 */
	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#execute(java.lang.String, boolean, boolean)
	 */
	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean1, boolean paramBoolean2)
			throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.gemstone.gemfire.cache.execute.Execution#execute(java.lang.String, boolean, boolean, boolean)
	 */
	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean1, boolean paramBoolean2,
			boolean paramBoolean3) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

}

package gedi.solutions.geode.functions;

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

	public ResultCollector<?, ?> execute(String paramString) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}


	public ResultCollector<?, ?> execute(Function paramFunction) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean1, boolean paramBoolean2)
			throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public ResultCollector<?, ?> execute(String paramString, boolean paramBoolean1, boolean paramBoolean2,
			boolean paramBoolean3) throws FunctionException
	{
		// TODO Auto-generated method stub
		return null;
	}

}

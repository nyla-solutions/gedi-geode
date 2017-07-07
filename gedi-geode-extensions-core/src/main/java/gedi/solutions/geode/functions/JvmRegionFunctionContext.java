package gedi.solutions.geode.functions;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;

public class JvmRegionFunctionContext<K, V,T> implements RegionFunctionContext
{
	
	public JvmRegionFunctionContext()
	{
	}

	public JvmRegionFunctionContext(Region<K,V> dataSet,ResultSender<T> resultSender, Object arguments, Set<?> filter)
	{
		this.dataSet = dataSet;
		this.resultSender = resultSender;
		this.filter = filter;
		this.arguments = arguments;
	}//-------------------------------------------------------------------
	
	public Object getArguments()
	{
		return this.arguments;
	}

	public String getFunctionId()
	{
		return this.functionId;
	}

	@SuppressWarnings("unchecked")
	public ResultSender<T> getResultSender()
	{
		return this.resultSender;
	}

	public boolean isPossibleDuplicate()
	{
		return false;
	}

	public Set<?> getFilter()
	{
		return filter;
	}

	@SuppressWarnings("unchecked")
	public Region<K, V> getDataSet()
	{
		return dataSet;
	}
	
	/**
	 * @param functionId the functionId to set
	 */
	public void setFunctionId(String functionId)
	{
		this.functionId = functionId;
	}

	private String functionId;
	private Region<K, V> dataSet;
	private Object arguments;
	private Set<?> filter;
	private ResultSender<T> resultSender;

}

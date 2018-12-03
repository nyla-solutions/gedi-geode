package gedi.solutions.geode.lucene.function;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;
import org.apache.logging.log4j.LogManager;

public class SimpleLuceneSearchFunction implements Function<Object>
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5728514774900318029L;

	@Override
	public void execute(FunctionContext<Object> context)
	{
		Object args = context.getArguments();
		
		if (args == null)
			throw new IllegalArgumentException("args is required");
		
		
		if(!(args instanceof String[]))
			throw new FunctionException("Arguments type "+args.getClass().getName()+" must be of type String[]");

		String[] inputs =  (String[])args;
		
		if (inputs.length < 4)
			throw new IllegalArgumentException("4 Arguments expected");
		
		String indexName = inputs[0];
		String regionName = inputs[1];
		String queryString = inputs[2];
		String defaultField = inputs[3];
		
		try
		{
			LuceneService ls= LuceneServiceProvider.get(CacheFactory.getAnyInstance());
			
			LuceneQuery<Object, Object> query = ls.createLuceneQueryFactory().create(indexName, regionName, queryString, defaultField);
			
			context.getResultSender().lastResult(query.findValues());
		}
		catch (LuceneQueryException e)
		{
			LogManager.getLogger().error(e);
			
			throw new FunctionException(e.getMessage(),e);
			
		}
		catch (Exception e)
		{
			LogManager.getLogger().error(e);
			
			throw new FunctionException(e.getMessage());
		}
		
	}//------------------------------------------------
	
	@Override
	public String getId()
	{
		return "SimpleLuceneSearchFunction";
	}

}

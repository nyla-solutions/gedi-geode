package gedi.solutions.geode.io.search.functions;

import java.util.Collection;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.pdx.JSONFormatter;
import org.apache.geode.pdx.PdxInstance;
import org.apache.geode.pdx.PdxSerializationException;

import gedi.solutions.geode.io.search.GeodeLuceneSearch;
import gedi.solutions.geode.io.search.SearchPageCriteria;

public class GeodeSearchFunction implements Function<Object>
{
	@Override
	public String getId()
	{
	
		return "GeodeSearchFunction";
	}//------------------------------------------------

	/**
	 * 
	 */
	private static final long serialVersionUID = 1503556037688196358L;

	@Override
	public void execute(FunctionContext<Object> funtionContext)
	{
		Object args = funtionContext.getArguments();
		
		if (args == null)
			throw new IllegalArgumentException("arguments is required");
		
		SearchPageCriteria criteria = null;
		
		if(args instanceof PdxInstance)
		{
			PdxInstance  pdxInstance = (PdxInstance)args;
			
			try
			{
				criteria = (SearchPageCriteria)(pdxInstance.getObject());
			}
			catch (PdxSerializationException e)
			{
				throw new FunctionException(e.getMessage()+" JSON:"+JSONFormatter.toJSON(pdxInstance));
			}
		}
		else 
		{
			criteria = (SearchPageCriteria)args;
		}
		
		
		if(criteria.getPageRegionName() == null || criteria.getPageRegionName().length() == 0)
			throw new IllegalArgumentException("criteria.pageRegionName is required");
		
		
		Cache cache = CacheFactory.getAnyInstance();
		
		GeodeLuceneSearch geodeSearch = new GeodeLuceneSearch(cache);
	
		Region<String, Collection<?>> pageKeysRegion = cache.getRegion(criteria.getPageRegionName());
		
		pageKeysRegion = cache.getRegion(criteria.getPageRegionName());
		
		Collection<String> keys = geodeSearch.saveSearchResultsWithPageKeys(criteria, pageKeysRegion);
		
		
		funtionContext.getResultSender().lastResult(keys);
		

	}

}

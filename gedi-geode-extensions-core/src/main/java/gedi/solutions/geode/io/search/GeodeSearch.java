package gedi.solutions.geode.io.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.LuceneServiceProvider;

import nyla.solutions.core.data.MapEntry;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.util.BeanComparator;
import nyla.solutions.core.util.Organizer;

public class GeodeSearch
{
	/**
	 * 
	 * @param gemFireCache the cache
	 * @param pageRegion the region to save search results
	 */
	public GeodeSearch(GemFireCache gemFireCache)
	{
		this(LuceneServiceProvider.get(gemFireCache));
	}//------------------------------------------------
	/**
	 * 
	 * @param gemFireCache the cache
	 * @param pageRegion the region to save search results
	 */
	public GeodeSearch(LuceneService luceneService)
	{
		this.luceneService = luceneService;
	}
	/**
	 * 
	 * @param query ex: "name:John AND zipcode:97006"
	 * @param regionName the region name
	 * @param indexName the index name
	 * @param defaultField the field name
	 * @return collection of results
	 */
	public Collection<String>  searchWithPageKeys(TextPageCriteria criteria, Region<String,Collection<?>> pageKeysRegion)
	{
		if(criteria == null)
			return null;
		
		if(criteria.getQuery() == null)
			return null;
		
		try
		{	
			
			LuceneQuery<Object, Object> luceneQuery = luceneService.createLuceneQueryFactory()
			  .create(criteria.getIndexName(), 
			  criteria.getRegionName(), 
			  criteria.getQuery(), criteria.getDefaultField());
			
			 List<LuceneResultStruct<Object, Object>> list = luceneQuery.findResults();
			 
			 if(list == null || list.isEmpty())
				 return null;
			
			 String sortField =  criteria.getSortField();
			 BeanComparator beanComparator = null;
			 
			 if(sortField != null && sortField.trim().length() > 0 )
			 {
				 beanComparator = new BeanComparator(sortField);
			 }
			 
			 Collection<Map.Entry<Object,Object>> set =  new TreeSet<Map.Entry<Object,Object>>(beanComparator);
			  
			
			 list.parallelStream().forEach(e -> set.add(new MapEntry<Object,Object>(e.getKey(), e.getValue())));
			 
			 //now all records are sorted by value
			 
			 //add to pages
			 List<Collection<Object>> pagesCollection = Organizer.toKeyPages(set, criteria.getPageSize());
			 
			 //pagesCollection.forEach(p -> this.pageRegion.put(toKeyPage.apply(e), arg1));
			 
			 int pageIndex = 0;
			 String key = null;
			 ArrayList<String> keys = new ArrayList<String>(10);
			 for (Collection<Object> page : pagesCollection)
			{
				 //store in region
				 key = new StringBuilder().append(criteria.getId()).append("-").append(pageIndex++).toString();
				 
				 pageKeysRegion.put(key, page);
			}
			 
			 keys.trimToSize();
			 
			 return keys;
	
		}
		catch (LuceneQueryException e)
		{
			throw new SystemException(e);
		}		
	}//------------------------------------------------
	
	@SuppressWarnings("unchecked")
	public <T> Collection<T> researchSearchResults(TextPageCriteria criteria, int pageNumber, Region<?,?> region, Region<String,Collection<?>> pageRegion)
	{
		if(pageRegion == null )
			return null;
		
		Collection<?> regionKeys = pageRegion.get(criteria.toPageKey(pageNumber));
		
		if(regionKeys == null|| regionKeys.isEmpty())
			return null;
		
		return (Collection<T>)region.getAll(regionKeys);
	}
	
	private final LuceneService luceneService;

}

package gedi.solutions.geode.lucene;

import java.io.Serializable;
import java.util.Set;

import nyla.solutions.core.patterns.iteration.PageCriteria;

public class TextPageCriteria extends PageCriteria implements  Serializable
{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6542273931404555584L;
	/**
	 * @return the query
	 */
	public String getQuery()
	{
		return query;
	}
	/**
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
	}
	/**
	 * @return the indexName
	 */
	public String getIndexName()
	{
		return indexName;
	}
	/**
	 * @return the defaultField
	 */
	public String getDefaultField()
	{
		return defaultField;
	}
	/**
	 * @param query the query to set
	 */
	public void setQuery(String query)
	{
		this.query = query;
	}
	/**
	 * @param regionName the regionName to set
	 */
	public void setRegionName(String regionName)
	{
		this.regionName = regionName;
	}
	/**
	 * @param indexName the indexName to set
	 */
	public void setIndexName(String indexName)
	{
		this.indexName = indexName;
	}
	/**
	 * @param defaultField the defaultField to set
	 */
	public void setDefaultField(String defaultField)
	{
		this.defaultField = defaultField;
	}
	
	
	/**
	 * @return the sortField
	 */
	public String getSortField()
	{
		return sortField;
	}
	/**
	 * @param sortField the sortField to set
	 */
	public void setSortField(String sortField)
	{
		this.sortField = sortField;
	}

	public String toPageKey(int pageNumber)
	{
		return new StringBuilder().append(this.getId()).append("-").append(pageNumber).toString();
	}
	

	/**
	 * @return the pageRegionName
	 */
	public String getPageRegionName()
	{
		return pageRegionName;
	}
	/**
	 * @param pageRegionName the pageRegionName to set
	 */
	public void setPageRegionName(String pageRegionName)
	{
		this.pageRegionName = pageRegionName;
	}


	/**
	 * @return the filter
	 */
	public Set<?> getFilter()
	{
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(Set<?> filter)
	{
		this.filter = filter;
	}

	/**
	 * @return the sortDescending
	 */
	public boolean isSortDescending()
	{
		return sortDescending;
	}
	/**
	 * @param sortDescending the sortDescending to set
	 */
	public void setSortDescending(boolean sortDescending)
	{
		this.sortDescending = sortDescending;
	}


	/**
	 * @return the limit
	 */
	public int getLimit()
	{
		return limit;
	}
	/**
	 * @param limit the limit to set
	 */
	public void setLimit(int limit)
	{
		this.limit = limit;
	}



	private String query;
	private String regionName;
	private String pageRegionName;
	private String indexName;
	private String defaultField;
	private String sortField;
	private boolean sortDescending = false;
	
	//private String id;
	//private int pageSize;
	private Set<?> filter;
	private int limit;
	//private int pageNumber ;
}
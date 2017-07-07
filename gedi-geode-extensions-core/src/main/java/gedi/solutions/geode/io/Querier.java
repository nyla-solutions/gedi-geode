package gedi.solutions.geode.io;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.NameResolutionException;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryInvocationTargetException;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.TypeMismatchException;

import gedi.solutions.geode.functions.JvmRegionFunctionContext;

public class Querier
{
	/**
	 * 
	 * @param query the OQL query
	 * @return the collection of the return types
	 */
	public static <ReturnType> Collection<ReturnType> query(String query)
	{
		return query(query, null);
	}// ------------------------------------------------

	public static <ReturnType> Collection<ReturnType> query(String query, RegionFunctionContext rfc)
	{
		try
		{

			QueryService queryService = CacheFactory.getAnyInstance().getQueryService();

			// Create the Query Object.
			Query queryObj = queryService.newQuery(query);

			return query(queryObj, rfc);
		}
		catch (Exception e)
		{
			throw new RuntimeException("Cannot execute query:" + query + " ERROR:" + e.getMessage(), e);
		}

	}// -------------------------------------------------------------------

	@SuppressWarnings("unchecked")
	public static <ReturnType> Collection<ReturnType> query(Query queryObj, RegionFunctionContext rfc)
	throws FunctionDomainException, TypeMismatchException, NameResolutionException, QueryInvocationTargetException
	{
		SelectResults<ReturnType> selectResults;

		// Execute Query locally. Returns results set.

		if (rfc == null || JvmRegionFunctionContext.class.isAssignableFrom(rfc.getClass()))
		{
			selectResults = (SelectResults<ReturnType>) queryObj.execute();
			if (selectResults == null || selectResults.isEmpty())
				return null;

			ArrayList<ReturnType> results = new ArrayList<ReturnType>(selectResults.size());
			results.addAll(selectResults.asList());

			return results;
		}
		else
		{
			selectResults = (SelectResults<ReturnType>) queryObj.execute(rfc);

			if (selectResults == null || selectResults.isEmpty())
				return null;

			return selectResults;
		}

	}

}

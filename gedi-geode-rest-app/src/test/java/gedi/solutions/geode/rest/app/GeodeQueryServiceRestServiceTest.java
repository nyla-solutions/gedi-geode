package gedi.solutions.geode.rest.app;

import static org.junit.Assert.*;
import org.junit.Test;

import com.google.gson.GsonBuilder;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;
import gedi.solutions.geode.io.QuerierService;
import gedi.solutions.geode.rest.app.GeodeQueryServiceRestService;
import nyla.solutions.core.security.user.data.UserProfile;
import nyla.solutions.global.json.JSON;

/**
 * Testing for REST service
 * @author Gregory Green
 *
 */
public class GeodeQueryServiceRestServiceTest
{

	//@Test
	public void testQueryLimit()
	throws Exception
	{
		QuerierService qs = mock(QuerierService.class);
		
		Collection<Object> expected = new ArrayList<>();
		UserProfile pi = mock(UserProfile.class);
		expected.add(pi);
		
		when(qs.query(anyString(),any())).thenReturn(expected);
		
		GeodeQueryServiceRestService restService = new GeodeQueryServiceRestService();
		restService.querierService = qs;
		restService.gson = new GsonBuilder().create();
		
		
		String query = null;
		int limit = 0;
		
		String json = restService.queryLimit(query,limit);
		
		assertNull(json);
		
		query = "select * from /data";
		
		json = restService.queryLimit(query,limit);
		assertNotNull(json);
		
		System.out.println("json:"+json);
		assertTrue(json.length() > 0);
		
		JSON jsonObject = JSON.newInstance();
		
		Object[] results = jsonObject.fromJson(json, Object[].class);
		
		assertTrue(results != null && results.length > 0);
		
	
		//multiple results
		
		expected.add(pi);
	
		json = restService.queryLimit(query,limit);
		
		results = jsonObject.fromJson(json, Object[].class);
		
		assertTrue(results != null && results.length > 1);
		
	}
	@Test
	public void testQuery_non_pdx()
	throws Exception
	{
		QuerierService qs = mock(QuerierService.class);
		
		Collection<Object> expected = new ArrayList<>();
		String pi = "hello world";
		expected.add(pi);
		
		verifyQuery(qs, expected);
		
	
		
	}
	@Test
	public void testQuery_string_with_specialchars()
	throws Exception
	{
		QuerierService qs = mock(QuerierService.class);
		
		Collection<Object> expected = new ArrayList<>();
		String pi = "hello \" world~`!@#$%^&*()_-+= {[}]|\\:;'<,>.?/";
		expected.add(pi);
		
		verifyQuery(qs, expected);
	}
	private void verifyQuery(QuerierService qs, Collection<Object> expected) throws Exception
	{
		when(qs.query(anyString(),any())).thenReturn(expected);
		
		GeodeQueryServiceRestService restService = new GeodeQueryServiceRestService();
		restService.gson = new GsonBuilder().create();
		
		restService.querierService = qs;
		
		
		String query = "select * from /data";
		int limit = 0;
		
		String json = restService.queryLimit(query,limit);
		
		assertNotNull(json);
		
		System.out.println("json:"+json);
		assertTrue(json.length() > 0);
		
		JSON jsonObject = JSON.newInstance();
		
		Object[] results = jsonObject.fromJson(json, Object[].class);
		
		assertTrue(results != null && results.length > 0);
	}

	//------------------------------------------------
	@Test
	public void test_appendLimit()
	{
		GeodeQueryServiceRestService restService = new GeodeQueryServiceRestService();
		String query = null;
		int limit = -1;
		
		assertNull(restService.appendLimit(query, limit));
		
		limit = -1;
		query = "";
		assertEquals(query, restService.appendLimit(query, limit));
		limit = 0;
		assertEquals(query, restService.appendLimit(query, limit));
		limit = 10;
		String results = restService.appendLimit(query, limit);
		assertNotEquals(query, results);
		
		assertEquals(results, " limit "+limit);
		
		query ="select * ";

		assertEquals(restService.appendLimit("select * from /region", limit), 
		"select * from /region limit "+limit);
		
		assertEquals(restService.appendLimit("select * from /region where a = 1", limit), 
		"select * from /region where a = 1 limit "+limit);
		assertEquals(restService.appendLimit("select * from /region where a = 1 group by a", limit), 
		"select * from /region where a = 1 group by a limit "+limit);
		
		
		//--------------
		assertEquals(restService.appendLimit("select * from /region limit "+limit, limit), 
		"select * from /region limit "+limit);
		
		assertEquals(restService.appendLimit("select * from /region where a = 1 limit "+limit, limit), 
		"select * from /region where a = 1 limit "+limit);
		assertEquals(restService.appendLimit("select * from /region where a = 1 group by a limit "+limit, limit), 
		"select * from /region where a = 1 group by a limit "+limit);
		
		
	}
}

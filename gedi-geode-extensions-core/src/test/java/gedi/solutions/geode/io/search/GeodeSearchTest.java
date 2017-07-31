package gedi.solutions.geode.io.search;

import static org.junit.Assert.*;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collection;

public class GeodeSearchTest
{

	@SuppressWarnings("unchecked")
	@Test
	public void testSearchWithPageKeys()
	throws Exception
	{
		Region<String,Collection<?>> pageRegion = mock(Region.class);
		
		LuceneService luceneService = mock(LuceneService.class);
		
		LuceneQueryFactory factory = mock(LuceneQueryFactory.class);
		LuceneQuery<Object, Object> query = mock(LuceneQuery.class);
		
		when(luceneService.createLuceneQueryFactory()).thenReturn(factory);
		
		when(factory.create(any(), any(), any(),any())).thenReturn(query);
		
		ArrayList<LuceneResultStruct<Object, Object>> results = new ArrayList<LuceneResultStruct<Object, Object>>();
		
		LuceneResultStruct<Object, Object> luceneResultStruct = mock(LuceneResultStruct.class);
		
		results.add(luceneResultStruct);
		
		when(query.findResults()).thenReturn(results);
		
		GeodeSearch searcher = new GeodeSearch(luceneService);
		
		
		assertNull(searcher.searchWithPageKeys(null,pageRegion));
		
		TextPageCriteria criteria = new TextPageCriteria();
		assertNull(searcher.searchWithPageKeys(criteria,pageRegion));
		
		criteria.setIndexName("TestIndex");
		criteria.setQuery("test");
		
		assertNotNull(searcher.searchWithPageKeys(criteria,pageRegion));
		
	}

}

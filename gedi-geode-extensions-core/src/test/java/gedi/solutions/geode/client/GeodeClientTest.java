package gedi.solutions.geode.client;

import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionFactory;
import org.apache.geode.cache.query.CqQuery;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.pdx.PdxInstance;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Queue;

/**
 * Created by ggreen on 7/7/17.
 */

public class GeodeClientTest  {

    @Test
    public void geodeClient_ShouldQueueReturnResultsForCq()
            throws Exception
    {

        PdxInstance expected = null;
        ClientRegionFactory<?,?> factory = Mockito.mock(ClientRegionFactory.class);

        ClientCache clientCache = Mockito.mock(ClientCache.class);

        QueryService queryService = Mockito.mock(QueryService.class);

        CqQuery cqQuery =  Mockito.mock(CqQuery.class);
        Mockito.when(clientCache.getQueryService()).thenReturn(queryService);
        Mockito.when(queryService.newCq(Mockito.any(),Mockito.any(),Mockito.any())).thenReturn(cqQuery);
       // Mockito.when(cqQuery.)


        GeodeClient geodeClient = new GeodeClient(clientCache,factory);

        String cnName  = "myname";
        String oql = "select * from /my region";

        Queue<PdxInstance> queue = geodeClient.registerCq(cnName,oql);

        Assert.assertTrue(queue != null);

        PdxInstance pdxRow = queue.poll();

        Assert.assertEquals(expected,pdxRow);






    }
}

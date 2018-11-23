package gedi.solutions.geode.client;


import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import org.apache.geode.cache.EntryEvent;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


public class GeodeListenerIT
{
	
	private static GeodeClient client;
	private static LinkedBlockingQueue<Object> putQueue = new LinkedBlockingQueue<>();
	private static LinkedBlockingQueue<Object> deleteQueue = new LinkedBlockingQueue<>();
	
	@BeforeClass
	public static void setUp()
	{
		client = GeodeClient.connect();
		Consumer<EntryEvent<String, Object>> putConsumer = e -> {
			
			System.out.println("e:"+e);
			putQueue.add(e);
		};
		
		Consumer<EntryEvent<String, Object>> deleteConsumer = e -> {
			
			System.out.println("e:"+e);
			deleteQueue.add(e);
		};
		
		
		client.registerAfterPut("testEvents", putConsumer);
		client.registerAfterDelete("testEvents", deleteConsumer);
		
		client.getRegion("testEvents");
		
	
	}
	
	
	@Test
	@Ignore
	public void testCq() throws Exception
	{
		
		BlockingQueue<Object> queue = client.registerCq("testCq", "select * from /test");
		
		Object take = queue.take();
		
		System.out.println("take:"+take);
		
	}

	@Test
	public void test_registerlistenerAddd() throws Exception
	{	
		putQueue.take();
		
	}
	
	@Test
	public void test_registerlistenerDelete() throws Exception
	{
		deleteQueue.take();	
		
	}

}

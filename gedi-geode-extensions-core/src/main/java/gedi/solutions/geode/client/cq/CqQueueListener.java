package gedi.solutions.geode.client.cq;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.geode.cache.Operation;
import org.apache.geode.cache.query.CqEvent;
import org.apache.geode.cache.query.CqListener;
import org.apache.geode.cache.query.CqQuery;

import nyla.solutions.core.patterns.Disposable;
import nyla.solutions.core.util.Debugger;

public class CqQueueListener<E> extends LinkedList<E> 
implements CqListener, Queue<E>, Disposable
{
	 /**
	 * 
	 */
	private static final long serialVersionUID = -249306773716810501L;
	
	
	@SuppressWarnings("unchecked")
	public void onEvent(CqEvent cqEvent)
	  {
	    // org.apache.geode.cache Operation associated with the query op
	    Operation queryOperation = cqEvent.getQueryOperation();

	    if (queryOperation.isDestroy())
	    	return;
	    
	    // key and new value from the event
	    E entry = (E)cqEvent.getNewValue();
	    
	    this.add(entry);
	  }//------------------------------------------------
	 
	  public void onError(CqEvent cqEvent)
	  {
	  }//------------------------------------------------
	  @Override
	  public void close()
	  {
	  }//------------------------------------------------

	/**
	 * @param cqQuery the cqQuery to set
	 */
	public void setCqQuery(CqQuery cqQuery)
	{
		this.cqQuery = cqQuery;
	}

	@Override
	public void dispose()
	{
		if (cqQuery != null)
		{
			try { cqQuery.close(); } catch (Exception e){Debugger.println(e.getMessage());}
		}
	}//------------------------------------------------
	  
	private transient CqQuery cqQuery = null;
	 
}

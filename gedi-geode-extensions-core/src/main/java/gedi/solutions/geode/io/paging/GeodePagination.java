package gedi.solutions.geode.io.paging;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import org.apache.geode.cache.Region;
import nyla.solutions.core.patterns.iteration.PageCriteria;
import nyla.solutions.core.patterns.iteration.Pagination;
import nyla.solutions.core.patterns.iteration.Paging;
import nyla.solutions.core.patterns.iteration.PagingCollection;

/**
 * @author Gregory Green
 *
 */
public class   GeodePagination extends Pagination
{


	@SuppressWarnings("unchecked")
	protected GeodePagination(String id, Region<?, Collection<?>> pageRegion,BiFunction<?,?,?> toKeyFunction)
	{
		super(id);
		
		this.pageRegion = pageRegion;
		
		this.toKeyFunction = (BiFunction<Object, Object, Object>)toKeyFunction;
	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#store(java.lang.Object, nyla.solutions.core.patterns.iteration.PageCriteria)
	 */
	@Override
	public <ObjectType> void store(ObjectType storeObject, PageCriteria pageCriteria)
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#constructPaging(java.util.Iterator, nyla.solutions.core.patterns.iteration.PageCriteria, nyla.solutions.core.patterns.creational.RowObjectCreator)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public<IterationObjectType>  void constructPaging(Iterator<IterationObjectType> resultSet,
	PageCriteria pageCriteria)
	{

		for (Object i=null; resultSet.hasNext(); i = resultSet.next())
		{
			Object key = toKeyFunction.apply(i,pageCriteria);
			((Region)this.pageRegion).put(key, i);
		}
	}//------------------------------------------------

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#getPaging(nyla.solutions.core.patterns.iteration.PageCriteria, java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <ReturnTypes> Paging<ReturnTypes> getPaging(PageCriteria pageCriteria, Class<?> pageClass)
	{
		String key = new StringBuilder().append(pageCriteria.getId())
		.append("-").append(pageCriteria.getBeginIndex())
		.append("-").append(pageCriteria.getEndIndex()).toString();
		
		Collection<?> collection = this.pageRegion.get(key);
		if(collection == null )
			return null;
		
		if(collection.isEmpty())
		{
			this.pageRegion.remove(key);
			return null;
		}
		
		return new PagingCollection<ReturnTypes>((Collection<ReturnTypes>)collection,pageCriteria);
	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#count(nyla.solutions.core.patterns.iteration.PageCriteria)
	 */
	@Override
	public long count(PageCriteria pageCriteria)
	{
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#clear()
	 */
	@Override
	public void clear()
	{
		 this.pageRegion.clear();

	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#size()
	 */
	@Override
	public long size()
	{
		return this.pageRegion.size();
	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#cancel()
	 */
	@Override
	public void cancel()
	{
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see nyla.solutions.core.patterns.iteration.Pagination#getFutures()
	 */
	@Override
	public Set<Future<?>> getFutures()
	{
		return null;
	}

	private final Region<?, Collection<?>> pageRegion;

	private final BiFunction<Object,Object,Object> toKeyFunction;
}

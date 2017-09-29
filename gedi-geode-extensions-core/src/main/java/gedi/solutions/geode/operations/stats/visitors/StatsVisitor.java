package gedi.solutions.geode.operations.stats.visitors;

import gedi.solutions.geode.operations.stats.ArchiveInfo;
import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.SimpleValue;
import gedi.solutions.geode.operations.stats.TimeStampSeries;

public interface StatsVisitor
{

	default  void visitArchInfo(ArchiveInfo archiveInfo){System.out.println("archiveInfo:"+archiveInfo.getMachine());}
	
	default void visitResourceType(ResourceType resourceType){System.out.println("resourceType:"+resourceType.getName());}
	
	default  void visitTimeStampSeries(TimeStampSeries timeStampSeries){System.out.println("timeStampSeries:"+timeStampSeries);}
	
	default void visitResourceInst(ResourceInst resourceInst){System.out.println("resourceInst:"+resourceInst.getName());}
	
	default void visitSimpleValue(SimpleValue simpleValue) {System.out.println("simpleValue:"+simpleValue);}
}

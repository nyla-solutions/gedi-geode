package gedi.solutions.geode.operations.stats.visitors;

import gedi.solutions.geode.operations.stats.ArchiveInfo;
import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.SimpleValue;
import gedi.solutions.geode.operations.stats.TimeStampSeries;

public interface StatsVisitor
{

	default  void visitArchInfo(ArchiveInfo archiveInfo){}
	
	default void visitResourceType(ResourceType resourceType){}
	
	default  void visitTimeStampSeries(TimeStampSeries timeStampSeries){}
	default void visitResourceInsts(ResourceInst[] resourceInsts){}
	default void visitResourceInst(ResourceInst resourceInst){}
	default void visitSimpleValue(SimpleValue simpleValue) {}
}

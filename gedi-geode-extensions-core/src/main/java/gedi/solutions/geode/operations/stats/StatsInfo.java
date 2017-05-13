package gedi.solutions.geode.operations.stats;

import gedi.solutions.geode.operations.stats.visitors.StatsVisitor;

public interface StatsInfo
{

	void accept(StatsVisitor visitor);
}

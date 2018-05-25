package gedi.solutions.geode.office;

import gedi.solutions.geode.operations.stats.visitors.StatsVisitor;
import nyla.solutions.office.chart.JFreeChartFacade;

public interface ChartStatsVisitor extends StatsVisitor
{

	/**
	 * @return the chart
	 */
	JFreeChartFacade getChart();
	

}
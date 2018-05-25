package gedi.solutions.geode.office;

import java.io.File;

import org.junit.Test;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;

public class ParNewCollectionTimeThresholdChartStatsVisitorTest
{

	@Test
	public void testGetChart()
	throws Exception
	{
		ParNewCollectionTimeThresholdChartStatsVisitor v = new ParNewCollectionTimeThresholdChartStatsVisitor();
		
		StatsToChart c = new StatsToChart(v);
		
		File f =  null;
		f = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/may-16-2018-noFunctionChange/server");
		//f = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/may-16-2018-noFunctionChange/server/tmp3/vantage-louweblqs159-server1/stats.gfs");
		Chart chart = c.convert(f);
		
    	IO.writeFile("target/NewPar.png", chart.getBytes());
	}

}

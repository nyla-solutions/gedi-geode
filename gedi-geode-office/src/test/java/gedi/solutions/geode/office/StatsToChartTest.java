package gedi.solutions.geode.office;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;

public class StatsToChartTest
{

	@Test
	public void testConvert()
	throws Exception
	{
		System.out.println(IO.delete(new File("target/graph.png")));
		StatsToChart c = new StatsToChart(new CpuAboveThresholdChartStatsVisitor());
		//File file = new File("src/test/resources/stats");
		File file = new File("/Projects/LifeSciences/Humana/docs/Vantage/docs/assessments/performance/perf-test-results/april-23-2018/tmp/support");
		
		
		Chart chart = c.convert(file);
		
		assertNotNull(chart);
		
		
		byte [] bytes = chart.getBytes();
		
		assertTrue(bytes != null  && bytes.length > 0);
		
		IO.writeFile("target/graph.png", bytes);
	}

}

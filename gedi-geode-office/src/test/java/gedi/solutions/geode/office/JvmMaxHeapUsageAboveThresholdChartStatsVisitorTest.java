package gedi.solutions.geode.office;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import java.nio.file.Paths;

import org.junit.Test;

import gedi.solutions.geode.operations.stats.ResourceInst;
import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;

public class JvmMaxHeapUsageAboveThresholdChartStatsVisitorTest
{

	@Test
	public void testVisitResourceInst()
	throws Exception
	{
		System.setProperty("DAY_FILTER", "04/12/2019");
		JvmMaxHeapUsageAboveThresholdChartStatsVisitor v = new JvmMaxHeapUsageAboveThresholdChartStatsVisitor();
		
		ResourceInst ri = mock(ResourceInst.class);
		
		v.visitResourceInst(ri);
		
		Chart c = v.getChart();
		
		String path = "target/out.png";
		IO.delete(Paths.get(path).toFile());
		
		IO.writeFile(path, c.getBytes(), false);
		assertTrue(IO.exists(path));
		
	}

}

package gedi.solutions.geode.office;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import nyla.solutions.core.io.IO;
import nyla.solutions.office.chart.Chart;

/**
 * <pre>
 * Usages 
 * 
 * java gedi.solutions.geode.offic.StatsToChartApp  <fileOrDirectoryWithStats> <pngFilePath>
 * </pre>
 */
public class StatsToChartApp 
{
    public static void main( String[] args )
    {
    	if(args.length != 2)
    	{
    		throw new IllegalArgumentException("Usage java "+StatsToChart.class.getName()+" <fileOrDirectoryWithStats> <pngFilePath>");
    		
    	}
		StatsToChart c = new StatsToChart();
		File file = new File(args[0]);
		
		System.out.println(file);
		
		Chart chart = c.convert(file);
		
		assertNotNull(chart);
		
		
		byte [] bytes = chart.getBytes();
		
		assertTrue(bytes != null  && bytes.length > 0);
		
		try
		{
			IO.writeFile(args[1], bytes);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }
}

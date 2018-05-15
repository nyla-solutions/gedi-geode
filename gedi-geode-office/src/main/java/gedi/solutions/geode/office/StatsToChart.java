package gedi.solutions.geode.office;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import gedi.solutions.geode.operations.stats.GfStatsReader;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.patterns.conversion.Converter;
import nyla.solutions.office.chart.Chart;

public class StatsToChart implements Converter<File, Chart>
{

	/**
	 * Accepts a file or directory contain the statistics files
	 * @param file the file or directory
	 * @return the chart for files
	 */
	public Chart convert(File file)
	{
		if(file == null)
			return null;
		
		try
		{
			CpuAboveThresholdChartStatsVisitor v = new CpuAboveThresholdChartStatsVisitor();
			
		    if(file.isDirectory())
		    {
		    	//Process for all files
		    	Set<File> statsFiles = IO.listFileRecursive(file, "*.gfs");
		    	if(statsFiles == null || statsFiles.isEmpty())
		    		return null;
		    	
		    	for (File statFile : statsFiles)
				{
		    		GfStatsReader reader = new GfStatsReader(statFile.getAbsolutePath());
			    	
			    	reader.accept(v);
				}
		    }
		    else
		    {
		    	GfStatsReader reader = new GfStatsReader(file.getAbsolutePath());
		    	
		    	reader.accept(v);
		    	
		    }
			
			

			

			
			return v.getChart();
		}
		catch (IOException e)
		{
			throw new RuntimeException ("File:"+file+" ERROR:"+e.getMessage(),e);
		}
	}

}

package gedi.solutions.geode.office;

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
    		throw new IllegalArgumentException("Usage java "+StatsToChart.class.getName()+" <fileOrDirectoryWithStats> <outputDirectory>");
    	}
    	
    	
    	try
		{
				
	    	
			StatsToChart cpuChartConvert = new StatsToChart(new CpuAboveThresholdChartStatsVisitor());
			File inputFileOrDirectory = new File(args[0]);
			
			System.out.println(inputFileOrDirectory);
			
			File cpuFilePath = new File(args[1]+"/cpu.png");
			File parNewCollectionTimesFilePath = new File(args[1]+"/parNewCollectionTimes.png");
			File parNewCollectionsFilePath = new File(args[1]+"/parNewCollections.png");
			
			Chart cpuChart = cpuChartConvert.convert(inputFileOrDirectory);
			System.out.println("Writing "+cpuFilePath.getAbsolutePath());
			IO.writeFile(cpuFilePath, cpuChart.getBytes());
			
			
			Chart parNewChart = new StatsToChart
			(new ParNewCollectionTimeThresholdChartStatsVisitor())
			.convert(inputFileOrDirectory);
			
			System.out.println("Writing "+parNewCollectionTimesFilePath.getAbsolutePath());
			IO.writeFile(parNewCollectionTimesFilePath, parNewChart.getBytes());
			
			Chart parNewCollections = new StatsToChart(new ParNewCollectionsChartStatsVisitor())
			.convert(inputFileOrDirectory);
			
			System.out.println("Writing "+parNewCollectionsFilePath.getAbsolutePath());
			IO.writeFile(parNewCollectionsFilePath, parNewCollections.getBytes());
			
			
			
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }//------------------------------------------------
}

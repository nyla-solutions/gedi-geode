package gedi.solutions.geode.operations.apps;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

import gedi.solutions.geode.operations.stats.GfStatsReader;
import gedi.solutions.geode.operations.stats.visitors.GenericCsvStatsVisitor;

public class StatsToCsvApp
{

	/**
	 * Main method to extract GF Stats to file
	 * @param args archiveFile csvFile [statName ]*
	 */
	public static void main(String[] args)
	{
		File archiveFile, csvFile;
		
		if(args.length < 1)
		{
			System.err.println("Usage: java "+GfStatsReader.class.getName()+" archiveFile [csvFile [statName ]*]");
			return;
		}
		try
		{
			archiveFile = Paths.get(args[0]).toFile();
			
			
			if(archiveFile.isDirectory())
			{
				GfStatsReader.toCvsFiles(archiveFile);
				return;
			}
					
			if(args.length < 2)
			{
				GfStatsReader reader = new GfStatsReader(archiveFile.getAbsolutePath());
				reader.dumpCsvFiles();
				return;
			}
		
			String typeName = args[1];
			
		
			csvFile = Paths.get(args[2]).toFile();
			
			GenericCsvStatsVisitor visitor = null;
			
			if(args.length > 3)
			{
				String[] stateNames = Arrays.copyOfRange(args, 2, args.length-1);
				visitor = new GenericCsvStatsVisitor(csvFile,typeName,stateNames);
			}
			else
				visitor = new GenericCsvStatsVisitor(csvFile,typeName);
			
			System.out.println("accepting");
			GfStatsReader reader = new GfStatsReader(archiveFile.getAbsolutePath());
			reader.accept(visitor);
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

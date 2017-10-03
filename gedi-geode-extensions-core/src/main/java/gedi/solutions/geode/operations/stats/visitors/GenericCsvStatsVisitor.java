package gedi.solutions.geode.operations.stats.visitors;




import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatDescriptor;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.core.io.csv.CsvWriter;

/**
 * Extracts details about regions
 * @author Gregory Green
 *
 */
public class GenericCsvStatsVisitor implements StatsVisitor
{
	
	private final String typeName;
	
	
	private String[]  statNames = null;
	private final File statsFile;
	private final File csvFile;
	
	private Path outputDirectory;
	
	public GenericCsvStatsVisitor(File file)
	{
		this.statsFile = file;
		this.csvFile = null;
		this.typeName = null;
		this.outputDirectory =  file.getParentFile().toPath();
	}//------------------------------------------------
	/**
	 * 
	 * @param file the STAT file
	 * @param typeName the type name
	 */
	public GenericCsvStatsVisitor(File file, String typeName)
	{
		this(file,typeName,(String[])null);
	}//------------------------------------------------
	/**
	 * 
	 * @param outputDirectory the output directory to set
	 */
	public void setOutputDirectory(Path outputDirectory)
	{
		this.outputDirectory = outputDirectory;
	}//------------------------------------------------
	public GenericCsvStatsVisitor(File file,String typeName, String... statNames)
	{
		if(typeName == null || typeName.length() == 0)
			this.typeName = null;
		else
			this.typeName = typeName.toUpperCase();
		
		this.statNames = statNames;
		
		this.statsFile = null;
		this.csvFile = file;
		
		this.outputDirectory = file.getParentFile().toPath();
	}//------------------------------------------------

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		String name = resourceInst.getName();
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip =  resourceType == null || resourceType.getName() == null || 
		(this.typeName != null && !resourceType.getName().toUpperCase().contains(this.typeName));
		
		if(skip)
		{
			System.out.println("skipping resourceType:"+resourceType+" name:"+name);
			return;
		}
		
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> headers = new ArrayList<String>();
		
		headers.add("name");
		values.add(name);
		
	
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
		for (StatValue statValue : statValues)
		{
			String statName = statValue.getDescriptor().getName();
			
			if(this.statNames != null && this.statNames.length > 0)
			{
				if(Arrays.binarySearch(statNames, statName) < 0)
					continue; //skip
			}
			StatValue dataStoreEntryCount = resourceInst.getStatValue(statName);

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);
			
			headers.add(statName+"        "+statDescriptor.getDescription());
			
			values.add(String.valueOf(dataStoreEntryCount.getSnapshotsMaximum()));
		}
		
		writeCsv(resourceInst,headers,values);
		
	}//------------------------------------------------
	
	
	void writeCsv(ResourceInst resourceInst,List<String> headers,List<String> values)
	{
		File file =  null;
		
		if(this.statsFile == null)
			file = csvFile;
		else
			file = Paths.get(this.outputDirectory.toFile().getAbsolutePath(), this.statsFile.getName()+"."+resourceInst.getType().getName()+".csv").toFile();
	
		CsvWriter csvWriter = new CsvWriter(file);
		
		try
		{
			csvWriter.writeHeader(headers);
			csvWriter.appendRow(values);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}//------------------------------------------------
}

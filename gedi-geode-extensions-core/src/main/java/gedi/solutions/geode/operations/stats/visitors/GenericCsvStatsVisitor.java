package gedi.solutions.geode.operations.stats.visitors;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.core.io.csv.CsvWriter;

/**
 * Extracts details about regions
 * @author Gregory Green
 *
 */
public class GenericCsvStatsVisitor implements StatsVisitor
{
	
	private final CsvWriter csvWriter;
	private final String typeName;
	
	
	private String[]  statNames = null;
	/**
	 * 
	 * @param file the STAT file
	 * @param typeName the type name
	 */
	public GenericCsvStatsVisitor(File file, String typeName)
	{
		this(file,typeName,(String[])null);
	}//------------------------------------------------
	public GenericCsvStatsVisitor(File file,String typeName, String... statNames)
	{
		if(typeName == null || typeName.length() == 0)
					throw new IllegalArgumentException("typeName is required");
	
		this.typeName = typeName.toUpperCase();
		
		this.statNames = statNames;
		csvWriter = new CsvWriter(file);
	}//------------------------------------------------

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
	
		String name = resourceInst.getName();
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip = resourceType == null || resourceType.getName() == null || !resourceType.getName().toUpperCase().contains(this.typeName);
		
		if(skip)
		{
			System.out.println("skipping resourceType:"+resourceType.getName()+" name:"+name);
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
			
			//System.out.println("JUNIT statName="+statName);
			
			if(this.statNames != null && this.statNames.length > 0)
			{
				if(Arrays.binarySearch(statNames, statName) < 0)
					continue; //skip
			}
			StatValue dataStoreEntryCount = resourceInst.getStatValue(statName);

			headers.add(statName+"        "+resourceInst.getType().getStat(statName).getDescription());
			
			values.add(String.valueOf(dataStoreEntryCount.getSnapshotsMaximum()));
		}
		
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

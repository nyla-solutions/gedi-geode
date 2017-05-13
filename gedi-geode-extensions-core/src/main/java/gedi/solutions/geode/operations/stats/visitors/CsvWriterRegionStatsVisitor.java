package gedi.solutions.geode.operations.stats.visitors;




import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.core.io.csv.CsvWriter;

public class CsvWriterRegionStatsVisitor implements StatsVisitor
{
	private final CsvWriter csvWriter;
	public CsvWriterRegionStatsVisitor(File file)
	{
		csvWriter = new CsvWriter(file);
	}

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		
		String name = resourceInst.getName();
		
		if(!resourceInst.getType().isRegion())
			return;
		
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> headers = new ArrayList<String>();
		
		headers.add("machine");
		headers.add("region");

		values.add(resourceInst.getArchive().getArchiveInfo().getMachine());
		values.add(name);
		
		
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
		/*
		 * dataStoreEntryCount
		 * dataStoreBytesInUse
		 * lowRedundancyBucketCount
		 * configuredRedundantCopies
		 * actualRedundantCopies
		 * localMaxMemory
		 */

		
		String[] statNames = {"dataStoreEntryCount","dataStoreBytesInUse"};
		
		for (String statName : statNames)
		{
			//String statName = statValue.getDescriptor().getName();
			
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
	}
}

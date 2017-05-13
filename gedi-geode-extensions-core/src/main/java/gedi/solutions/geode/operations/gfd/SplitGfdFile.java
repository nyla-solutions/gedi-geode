package gedi.solutions.geode.operations.gfd;

import java.io.File;
import java.io.IOException;

import org.apache.geode.internal.cache.snapshot.SnapshotPacket.SnapshotRecord;

import nyla.solutions.core.util.Config;

/**
 * 
 * Splits a export file into batches
 * 
 * @author Gregory Green
 *
 */
@Deprecated
public class SplitGfdFile implements Runnable
{
	/**
	 * Default constructor
	 */
	public SplitGfdFile()
	{
	}// ------------------------------------------------

	@Override
	public void run()
	{
		if (!outDirectory.exists())
		{
			throw new IllegalArgumentException(outDirectory + " does not exist");
		}

		// read

		try(SnapshotRecordReader reader = new SnapshotRecordReader(inFile))
		{
			
			SnapshotRecord record = null;

			// create writer
			int batchCount = 1;
			File out = createFile(batchCount++);
			SnapshotRecordWriter writer = null;
			
			try
			{
				writer = new SnapshotRecordWriter(out, regionName);
				int cnt = 0;
				while ((record = reader.readSnapshotRecord()) != null)
				{
					writer.writeSnapshotEntry(record);
					cnt++;
	
					if (cnt >= batchSize)
					{
						out = createFile(batchCount++);
						writer.close();
						
						writer = new SnapshotRecordWriter(out, regionName);
					}
				}
			}
			finally
			{
				if(writer != null)
					writer.close();
			}
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
	}// ------------------------------------------------

	File createFile(int batchCount)
	{
		return new File(outDirectory.getPath(), regionFileName + batchCount + ".gfd");

	}

	public static void main(String[] args) throws Exception
	{
		if(args == null || args.length == 0)
		{
			System.err.println("Usage: java "+SplitGfdFile.class.getName()+" region file outDir batchSize");
			System.exit(-1);
		}
		SplitGfdFile split = new SplitGfdFile();

		String regionName = args[0];
		File file = new File(args[1]);
		File directory = new File(args[2]);

		split.setRegionName(regionName);
		split.setInFile(file);
		split.setOutDirectory(directory);

		int batchSize = Integer.valueOf(args[3]);
		split.setBatchSize(batchSize);
		
		split.run();

	}

	/**
	 * @return the outDirectory
	 */
	public File getOutDirectory()
	{
		return outDirectory;
	}

	/**
	 * @return the inFile
	 */
	public File getInFile()
	{
		return inFile;
	}

	/**
	 * @return the regionName
	 */
	public String getRegionName()
	{
		return regionName;
	}

	/**
	 * @param outDirectory
	 *            the outDirectory to set
	 */
	public void setOutDirectory(File outDirectory)
	{
		this.outDirectory = outDirectory;
	}

	/**
	 * @param inFile
	 *            the inFile to set
	 */
	public void setInFile(File inFile)
	{
		this.inFile = inFile;
	}

	/**
	 * @param regionName
	 *            the regionName to set
	 */
	public void setRegionName(String regionName)
	{

		this.regionName = regionName;

		regionFileName  = this.regionName;
		
		if (this.regionFileName.startsWith("/"))
		{
			this.regionFileName = regionName.substring(1);
		}

		regionFileName = regionFileName.replace('/', '-');
	}// ------------------------------------------------

	/**
	 * @return the batchSize
	 */
	public int getBatchSize()
	{
		return batchSize;
	}

	/**
	 * @param batchSize
	 *            the batchSize to set
	 */
	public void setBatchSize(int batchSize)
	{
		this.batchSize = batchSize;
	}

	private File outDirectory;
	private File inFile;
	private String regionName;
	private String regionFileName;
	private int batchSize = Config.getPropertyInteger(SplitGfdFile.class, "bathSize", 100);
}

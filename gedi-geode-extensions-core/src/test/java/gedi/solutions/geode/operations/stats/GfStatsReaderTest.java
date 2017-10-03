package gedi.solutions.geode.operations.stats;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;

import org.junit.Test;
import static org.junit.Assert.*;

import gedi.solutions.geode.operations.stats.ArchiveInfo;
import gedi.solutions.geode.operations.stats.visitors.RegionCsvStatsVisitor;
import nyla.solutions.core.io.IO;

public class GfStatsReaderTest
{
	@Test
	public void testMain()
	throws Exception
	{
		String csvFilePath = "runtime/VMStats.csv";
		
		String archiveName = "src/test/resources/stats/server1.gfs";
		
		if(IO.delete(Paths.get(csvFilePath).toFile()))
		{
			System.out.println("file deleted");
		}
		
		String [] args = {archiveName,"VMStats",csvFilePath};
		GfStatsReader.main(args);
		
		File file = Paths.get(csvFilePath).toFile();
		assertTrue(file.exists());
		assertTrue(file.delete());
		
		args[1] = "VMMemoryUsageStats";
		//svFilePath = "runtime/VMStats.csv";
		
	}
	@Test
	public void testMainJustStat()
	throws Exception
	{
		
		String archiveName = "src/test/resources/stats/server1.gfs";
	
		String [] args = {archiveName};
		GfStatsReader.main(args);
		

		
	}
	@Test
	public void testDump()
	throws Exception
	{
		String archiveName = "src/test/resources/stats/server1.gfs";
		
		GfStatsReader reader = new GfStatsReader(archiveName);
		
		ArchiveInfo archiveInfo = reader.getArchiveInfo();
		
		System.out.println("archiveInfo:"+archiveInfo);
		reader.dump(new PrintWriter(System.out));
	}
	
	@Test
	public void testPrettyPRint()
	throws Exception
	{
		String archiveName = null;
		GfStatsReader reader = null;

		File file = new File("runtime/out.csv");
		file.delete();
		
		RegionCsvStatsVisitor visitor = new RegionCsvStatsVisitor(file);
		for(int i= 27; i <= 38;i++)
		{
			archiveName = "src/test/resources/stats/rdrlnxm"+i+"-server1.gfs";
			reader = new GfStatsReader(archiveName);
			reader.accept(visitor);
		}
		
		for(int i= 48; i <= 55;i++)
		{
			archiveName = "src/test/resources/stats/rdrlnxm"+i+"-server1.gfs";
			reader = new GfStatsReader(archiveName);
			reader.accept(visitor);
		}
		
		
	}

}

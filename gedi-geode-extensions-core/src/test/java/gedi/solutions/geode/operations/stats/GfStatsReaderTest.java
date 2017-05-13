package gedi.solutions.geode.operations.stats;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Test;

import gedi.solutions.geode.operations.stats.ArchiveInfo;
import gedi.solutions.geode.operations.stats.visitors.CsvWriterRegionStatsVisitor;

public class GfStatsReaderTest
{

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
		
		CsvWriterRegionStatsVisitor visitor = new CsvWriterRegionStatsVisitor(file);
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

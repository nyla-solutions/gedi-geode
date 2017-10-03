package gedi.solutions.geode.operations.stats.visitors;

import static org.junit.Assert.*;

import java.io.File;
import java.nio.file.Paths;

import org.junit.Test;
import static org.mockito.Mockito.*;

import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatDescriptor;
import gedi.solutions.geode.operations.stats.StatValue;

public class GenericCsvStatsVisitorTest
{

	@Test
	public void testGenericCsvStatsVisitorFile()
	{
		String resourceTypeName = "junitResourceType";
		String resourceInstName = "junitResourceInstName";
		
		String expectFileName =  "target/stats/server1.gfs."+resourceTypeName+".csv";
		
		if(Paths.get(expectFileName).toFile().delete())
		{
			System.out.println("Old file deleleted");
		}
		
		File file = Paths.get("src/test/resources/stats/server1.gfs").toFile();
		
		GenericCsvStatsVisitor v = new GenericCsvStatsVisitor(file);
		v.setOutputDirectory(Paths.get("target/stats/"));
		
		StatValue stat1 = mock(StatValue.class);
		StatDescriptor stat1Descriptor = mock(StatDescriptor.class);
		when(stat1Descriptor.getName()).thenReturn("stat1");
		when(stat1.getSnapshotsMaximum()).thenReturn(Double.valueOf(10));
		when(stat1.getDescriptor()).thenReturn(stat1Descriptor);
		
		StatValue stat2 = mock(StatValue.class);

		StatDescriptor stat2Descriptor = mock(StatDescriptor.class);
		when(stat2Descriptor.getName()).thenReturn("stat2");
		
		when(stat2.getSnapshotsMaximum()).thenReturn(Double.valueOf(20));
		when(stat2.getDescriptor()).thenReturn(stat2Descriptor);

		ResourceType resourceType = mock(ResourceType.class);
		when(resourceType.getName()).thenReturn(resourceTypeName);
	
		v.visitResourceType(resourceType);
		
		ResourceInst resourceInst = mock(ResourceInst.class);
		when(resourceInst.getName()).thenReturn(resourceInstName);
		when(resourceInst.getType()).thenReturn(resourceType);
		when(resourceInst.getStatValue("stat1")).thenReturn(stat1);
		when(resourceInst.getStatValue("stat2")).thenReturn(stat2);
		
		when(resourceType.getStat("stat1")).thenReturn(stat1Descriptor);
		when(resourceType.getStat("stat2")).thenReturn(stat2Descriptor);
		
		StatValue[] statValues = { stat1,stat2};

		when(resourceInst.getStatValues()).thenReturn(statValues);
		v.visitResourceInst(resourceInst);
		
		assertTrue(Paths.get(expectFileName).toFile().exists());
	}

}

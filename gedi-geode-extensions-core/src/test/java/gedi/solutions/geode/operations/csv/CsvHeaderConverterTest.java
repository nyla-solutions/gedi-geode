package gedi.solutions.geode.operations.csv;

import static org.junit.Assert.*;

import org.junit.Test;

import gedi.solutions.geode.demo.SimpleObject;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;

public class CsvHeaderConverterTest
{

	@Test
	public void testConvert()
	{
		CsvHeaderConverter c = new CsvHeaderConverter();
		
		assertEquals("\"\"\n",c.convert(null));
		assertEquals(String.class.getSimpleName()+"\n",c.convert(""));
		
		JavaBeanGeneratorCreator<SimpleObject> factory = new JavaBeanGeneratorCreator<>(SimpleObject.class);
		
		SimpleObject so = factory.create();
		
		String out = c.convert(so);
		
		System.out.println("out:"+out);
		
		assertNotNull(out);
		assertEquals("\"bigDecimal\",\"error\",\"exception\",\"fiedByte\",\"fieldBooleanObject\",\"fieldByteObject\",\"fieldCalendar\",\"fieldChar\",\"fieldCharObject\",\"fieldClass\",\"fieldDate\",\"fieldDouble\",\"fieldDoubleObject\",\"fieldFloat\",\"fieldFloatObject\",\"fieldInt\",\"fieldInteger\",\"fieldLong\",\"fieldLongObject\",\"fieldShort\",\"fieldShortObject\",\"fieldSqlDate\",\"fieldString\",\"fieldTime\",\"fieldTimestamp\",\"getWithNoSet\",\"overloadedRestriction\",\"simpleEnum\"\n",out);
	}

}

package gedi.solutions.geode.operations.csv;

import java.util.Collection;
import java.util.TreeSet;

import org.apache.geode.pdx.PdxInstance;

import nyla.solutions.core.io.csv.BeanPropertiesToCsvHeaderConverter;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.conversion.Converter;

public class CsvHeaderConverter implements Converter<Object, String>
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String convert(Object sourceObject)
	{
		Class<?> cls = sourceObject.getClass();
		
		if(!PdxInstance.class.isAssignableFrom(cls))
		{
			if(beanPropertiesToCsvHeaderConverter == null)
				beanPropertiesToCsvHeaderConverter = new BeanPropertiesToCsvHeaderConverter();
			
			
			return beanPropertiesToCsvHeaderConverter.convert(sourceObject);
		}
		
		PdxInstance pdxInstance = (PdxInstance)sourceObject;
		Collection<String> fieldsList = pdxInstance.getFieldNames();
		

		if(fieldsList == null)
			return null;
		
		fieldsList = new TreeSet<String>(fieldsList);
		
		StringBuilder row = new StringBuilder();
		
		
		for (String fieldName : fieldsList)
		{	
			CsvWriter.addCell(row,fieldName);
		}
		row.append("\n");
		
		return row.toString();
	}

	private BeanPropertiesToCsvHeaderConverter<Object> beanPropertiesToCsvHeaderConverter = null;
}

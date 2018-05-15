package gedi.solutions.geode.operations.csv;

import java.util.Collection;
import java.util.TreeSet;

import org.apache.geode.pdx.PdxInstance;

import nyla.solutions.core.io.csv.BeanPropertiesToCsvConverter;
import nyla.solutions.core.io.csv.CsvWriter;
import nyla.solutions.core.patterns.conversion.Converter;

/**
 * Converts PDX instance of java beans properties to CSV lines
 * 
 * @author Gregory Green
 *
 */
public class CsvRowConverter implements Converter<Object, String>
{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String convert(Object sourceObject)
	{
		Class<?> cls = sourceObject.getClass();
		
		if(!PdxInstance.class.isAssignableFrom(cls))
		{
			if(beanPropertiesToCsvConverter == null)
				beanPropertiesToCsvConverter = new BeanPropertiesToCsvConverter(cls);
			
			
			return beanPropertiesToCsvConverter.convert(sourceObject);
		}
		
		PdxInstance pdxInstance = (PdxInstance)sourceObject;
		Collection<String> fieldsList = pdxInstance.getFieldNames();
		
		if(fieldsList == null)
			return null;
		
		fieldsList = new TreeSet<String>(fieldsList);
		
		StringBuilder row = new StringBuilder();
		
		Object field = null;
		for (String fieldName : fieldsList)
		{
			field = pdxInstance.getField(fieldName);
			
			CsvWriter.addCell(row,field ==  null? "" : field.toString());
		}
		
		row.append("\n");
		
		return row.toString();
	}

	private BeanPropertiesToCsvConverter<Object> beanPropertiesToCsvConverter = null;
}

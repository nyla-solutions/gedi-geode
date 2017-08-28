package gedi.solutions.geode.operations.functions;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Set;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.RegionFunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.partition.PartitionRegionHelper;

import gedi.solutions.geode.operations.csv.CsvHeaderConverter;
import gedi.solutions.geode.operations.csv.CsvRowConverter;
import nyla.solutions.core.exception.SystemException;
import nyla.solutions.core.io.converter.ConversionFileAuthor;
import nyla.solutions.core.util.Config;


/**
 * <pre>
 * 
 * Exports the CSV string of all region data on the server.
 * 
 * The output will be stored in the $gbc-cacheserver-{PERSISTENT_STORE_LOCATION} indicated directory.
 * One file will be written per region (format: ${region.name}.json)
 * 
 * Example:			gfsh&gt;execute function --id="ExportJsonFunction" --arguments=myRegion
 * </pre>
 * 
 * @author Gregory Green
 *
 */
public class ExportCsvFunction  implements Function
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5996297280142495515L;

	private String directoryPath = Config.getProperty(this.getClass(),"directoryPath",".");
	
	private static String fileSeparator = System.getProperty("file.separator");
	private static String suffix = ".csv";
	
	
	
	public ExportCsvFunction()
	{
	}// ------------------------------------------------
	
	/**
	 * Export region data in JSON format
	 */
	public void execute(FunctionContext fc)
	{
		
		ResultSender<Object> rs = fc.getResultSender();
		try
		{
			boolean didExport = false;
			
			if(fc instanceof RegionFunctionContext)
			{
				didExport = exportOnRegion((RegionFunctionContext)fc);	
			}
			else
			{
				didExport = exportAllRegions(fc);
			}
			
			rs.lastResult(didExport);
		}
		catch (Exception e)
		{
			
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			
			CacheFactory.getAnyInstance().getLogger().error(sw.toString());
			rs.sendException(e);
		}
		
	    
	}// --------------------------------------------------------
	private boolean exportAllRegions(FunctionContext fc)
	{
		
		  String[] args = (String[]) fc.getArguments();
		    
		    if(args == null || args.length == 0)
		    {
		    	throw new FunctionException("Argument not provided");
		    }
		    
			
		//Get region name from arguments
		String regionName = args[0];
		
	       
		Cache cache = CacheFactory.getAnyInstance();
		
		Region<Object,Object> region = cache.getRegion(regionName);
		
		return exportRegion(region);
	
	}// ------------------------------------------------

	private boolean exportOnRegion(RegionFunctionContext rfc)
	{
		//get argument 
		
		//check if region is partitioned
	
		Region<Object,Object> region = rfc.getDataSet();
	    
		
	    return exportRegion(region);
	}// ------------------------------------------------

	private boolean  exportRegion(Region<Object, Object> region)
	{
		
		if(PartitionRegionHelper.isPartitionedRegion(region))
		{
			region = PartitionRegionHelper.getLocalData(region);
		}
		
		//get first
	    

	    Set<Object> keySet = region.keySet();
	    
	    if(keySet == null || keySet.isEmpty())
	    {
	    		return false;	    	
	    }
	    
	    String regionName = region.getName();
	 
	    String memberName = CacheFactory.getAnyInstance().getDistributedSystem().getDistributedMember().getName();
	    
	    
	    File resultFile = new File(new StringBuilder(this.directoryPath)
				.append(fileSeparator).append(memberName).append("-").append(regionName).append(suffix).toString());
	    
	    
	    if(resultFile.exists())
	    {
	    		if(!resultFile.delete())
	    			throw new SystemException("Cannot delete:"+resultFile);
	    }
	    
	    ConversionFileAuthor<Object> author = new ConversionFileAuthor<>(resultFile, new CsvHeaderConverter(), new CsvRowConverter());
	    
	    try
		{
	    	
	    	Object value = null;
	    	
			for (Object key : keySet)
			{
				value = region.get(key);
				
				author.appendFile(value);
			}
			
			return true;
			
		}
		catch (RuntimeException e)
		{
			throw e;
		}
	    catch(IOException e)
	    {
	    	throw new FunctionException("Error exporting ERROR:"+ e.getMessage(),e);
	    }
	}// ------------------------------------------------

	
	public String getId()
	{
		
		return "ExportCsvFunction";
	}

	public boolean hasResult()
	{
		return true;
	}

	public boolean isHA()
	{
		return false;
	}

	public boolean optimizeForWrite()
	{
		return true;
	}
	
}

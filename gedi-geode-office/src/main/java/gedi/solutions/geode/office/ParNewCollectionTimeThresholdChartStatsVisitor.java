package gedi.solutions.geode.office;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatDescriptor;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.core.data.Property;
import nyla.solutions.core.data.clock.Day;
import nyla.solutions.core.util.Text;
import nyla.solutions.office.chart.JFreeChartFacade;

/**
 * <pre>
 *  
 *  The class will generate a bar graph of all ParNew collection times greater 
 *  than a given millisecond threshold.
 *  
 * </pre>
 * @author Gregory Green
 *
 */
public class ParNewCollectionTimeThresholdChartStatsVisitor  implements  ChartStatsVisitor
{
	
	public ParNewCollectionTimeThresholdChartStatsVisitor()
	{
		this.chart =new JFreeChartFacade();
		this.chart.setTitle("Max Par New collection time per second on "+StatsConstants.DAY_FILTER);
		this.chart.setGraphType("bar");
		this.chart.setHeight(2000);
		this.chart.setWidth(2000);
		
		this.chart.setLegend(true);
		this.chart.setTooltips(true);
	}

	@Override
	public void visitResourceInsts(ResourceInst[] resourceInsts)
	{
		this.appName = StatsUtil.getAppName(resourceInsts);
	}

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		
		
		String name = resourceInst.getName();
		
		
		//String machine = StatsUtil.formatMachine(resourceInst.getArchive().getArchiveInfo().getMachine());
		
		//String system = resourceInst.getArchive().getArchiveInfo().getSystem();
		
		System.out.println("system:"+appName);
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip = !this.resourceResourceNameFilter.equals(name) ||  resourceType == null || resourceType.getName() == null || 
		(this.filterTypeName != null && !resourceType.getName().toUpperCase().contains(this.filterTypeName));
		
		if(skip)
		{
			return;
		}

		
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
		for (StatValue statValue : statValues)
		{
			String statName = statValue.getDescriptor().getName();

			
			if(filterStatName != null && !filterStatName.equalsIgnoreCase(statName))
			{
				continue;  //skip;
			}
			

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);
			System.out.println("app:"+this.appName+" name:"+name+" resourceType"+resourceType.getName()+".statName:"+statName+" describe:"+statDescriptor.getDescription()+
			" units:"+statDescriptor.getUnits() );
			
			
			long [] times = statValue.getRawAbsoluteTimeStamps();
			double [] values = statValue.getSnapshots();
			
			
			String timeFormat = "HH:mm:ss";
			
			
			for (int i = 0; i < values.length; i++)
			{
				
				Date date = new Date(times[i]);
			
				Day day = new Day(date);
				if(!StatsConstants.DAY_FILTER.isSameDay(day))
					continue;
					
				String timeValueText = Text.formatDate(timeFormat,date);
				Property timeValue = new Property(this.appName,timeValueText);
				
				//get previous Max
				Double max = this.maxMap.get(timeValue);
				
				if(max == null)
					max = Double.valueOf(values[i]);
				else
					max = Double.valueOf(Math.max(values[i], max.doubleValue()));
				
				if(max.doubleValue() > statValue.getSnapshotsMaximum() )
					throw new IllegalArgumentException(max.doubleValue()+">"+statValue.getSnapshotsMaximum()+" statValue:"+statValue);
				
				if(values[i] >= threshold)
				this.maxMap.put(timeValue, max);				
			}
			
		}
		
		for (Map.Entry<Property, Double> entry : maxMap.entrySet())
		{
			this.chart.plotValue(entry.getValue(), 
			entry.getKey().getName(),
			entry.getKey().getValue().toString());
		}
		
	}//------------------------------------------------
	


	/**
	 * @return the chart
	 */
	public JFreeChartFacade getChart()
	{
		return chart;
	}//------------------------------------------------
	
	private String appName = null;
	private String resourceResourceNameFilter = "ParNew";
	private String filterTypeName = "VMGCStats".toUpperCase();
	private String filterStatName = "collectionTime";
	private Map<Property, Double> maxMap = new HashMap<>();

	private  final JFreeChartFacade chart ;
	private Double threshold = Double.valueOf(50.1);
	
}

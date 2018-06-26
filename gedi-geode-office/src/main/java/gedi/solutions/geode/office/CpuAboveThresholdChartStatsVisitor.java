package gedi.solutions.geode.office;


import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

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
 * </pre>
 * @author Gregory Green
 *
 */
public class CpuAboveThresholdChartStatsVisitor implements  ChartStatsVisitor
{
	
	public CpuAboveThresholdChartStatsVisitor()
	{
		String title = "CPU per minute over 50% usage "+
		Text.formatDate("MM/dd/yyyy",StatsConstants.DAY_FILTER.getDate());
		
		this.chart =new JFreeChartFacade();
		this.chart.setTitle(title);

		this.chart.setGraphType("bar");
		this.chart.setHeight(1000);
		this.chart.setWidth(7000);
		//this.chart.setCategoryLabel(this.filterStatName);
	}


	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		String name = resourceInst.getName();
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip =  resourceType == null || resourceType.getName() == null || 
		(this.filterTypeName != null && !resourceType.getName().toUpperCase().contains(this.filterTypeName));
		
		if(skip)
		{
			//System.out.println("skipping resourceType:"+resourceType+" name:"+name);
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
			
			StatValue dataStoreEntryCount = resourceInst.getStatValue(statName);

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);
			System.out.println("name:"+name+" resourceType"+resourceType.getName()+".statName:"+statName+" describe:"+statDescriptor.getDescription());

			long [] times = dataStoreEntryCount.getRawAbsoluteTimeStamps();
			double [] values = dataStoreEntryCount.getSnapshots();
			
			
			String timeFormat = "HH:mm";
			
			
			for (int i = 0; i < values.length; i++)
			{
				
				Date date = new Date(times[i]);
			
				Day day = new Day(date);
				if(!StatsConstants.DAY_FILTER.isSameDay(day))
					continue;
					
				String timeValueText = Text.formatDate(timeFormat,date);
				Property timeValue = new Property(name,timeValueText);
				
				//get previous Max
				Double max = this.maxMap.get(timeValue);
				
				if(max == null)
					max = Double.valueOf(values[i]);
				else
					max = Double.valueOf(Math.max(values[i], max.doubleValue()));
				
				this.maxMap.put(timeValue, max);
				
				if(values[i] >= cpuPercentThreshold)
					this.chart.plotValue(max, timeValue.getName(), (String)timeValue.getValue());
			}
			
		}
		
	}//------------------------------------------------
	


	/* (non-Javadoc)
	 * @see gedi.solutions.geode.office.ChatStatsVisitor#getChart()
	 */
	@Override
	public JFreeChartFacade getChart()
	{
		return chart;
	}//------------------------------------------------
	private Map<Property,Double> maxMap = new TreeMap<Property,Double>();
	private String filterTypeName = "LinuxSystemStats".toUpperCase();
	private String filterStatName = "cpuActive";
	private double cpuPercentThreshold = 50.1;

	private  final JFreeChartFacade chart ;
	
}

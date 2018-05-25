package gedi.solutions.geode.office;


import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatDescriptor;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.core.data.NumberedProperty;
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
public class ParNewCollectionsChartStatsVisitor implements  ChartStatsVisitor
{
	


	public ParNewCollectionsChartStatsVisitor()
	{
		String title = "Collections per second greater than "+threshold+" "+
		Text.formatDate("MM/dd/yyyy",StatsConstants.DAY_FILTER.getDate());
		
		this.chart =new JFreeChartFacade();
		this.chart.setTitle(title);

		this.chart.setGraphType("bar");
		this.chart.setHeight(1000);
		this.chart.setWidth(2000);
		this.chart.setTooltips(true);
		this.chart.setLegend(true);
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
		
		
		
		ResourceType resourceType= resourceInst.getType();
		
		boolean skip = !this.resourceResourceNameFilter.equals(name) ||  resourceType == null || resourceType.getName() == null || 
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
			

			StatDescriptor statDescriptor = resourceInst.getType().getStat(statName);
			System.out.println("appName:"+appName+" name:"+name+" resourceType"+resourceType.getName()+".statName:"+statName+" describe:"+statDescriptor.getDescription()+
			" units:"+statDescriptor.getUnits() );
			
			long [] times = statValue.getRawAbsoluteTimeStamps();
			//double [] values = statValue.getRawSnapshots();
			double [] values = statValue.getSnapshots();
			
			
			String timeFormat = "HH:MM:ss";
			
			NumberedProperty current = null;
			Date date  = null;
			Day day = null;
			int newValue;
			
			for (int i = 0; i < values.length; i++)
			{
				
			    date = new Date(times[i]);
				day = new Day(date);
				
				if(!StatsConstants.DAY_FILTER.isSameDay(day))
					continue;
					
				String timeValue = Text.formatDate(timeFormat,date);
				
				
				NumberedProperty value = this.countPerHour.get(timeValue);
				
				 current = new NumberedProperty(appName,Integer.valueOf(Double.valueOf(values[i]).intValue()));
				
				if(value == null)
					value = current;
				else
				{
					newValue = Integer.valueOf(value.getNumber() + current.getNumber());
					value.setNumber(newValue);
				}
				countPerHour.put(timeValue, value);						
			}

		}
		
		String entryName = null;
		int intValue;
		for(Map.Entry<String, NumberedProperty> entry : this.countPerHour.entrySet())
		{
			entryName = entry.getValue().getName();
			entryName = entryName.replace("amd64 ", "");
			System.out.println("entryName:"+entryName);
			
			intValue = entry.getValue().getValueInteger();
			
			if(intValue > threshold)
			{
				this.chart.plotValue(intValue, entryName, entry.getKey());
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

	private  Map<String,NumberedProperty> countPerHour = new TreeMap<>();
	private String resourceResourceNameFilter = "ParNew";
	private String filterTypeName = "VMGCStats".toUpperCase();
	private String filterStatName = "collections";
	private String appName;
	private  final JFreeChartFacade chart ;
	private  int threshold = 1;
	
}

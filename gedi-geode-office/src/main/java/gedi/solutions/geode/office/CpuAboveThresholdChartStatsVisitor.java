package gedi.solutions.geode.office;


import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatDescriptor;
import gedi.solutions.geode.operations.stats.StatValue;
import gedi.solutions.geode.operations.stats.visitors.StatsVisitor;
import nyla.solutions.office.chart.JFreeChartFacade;

public class CpuAboveThresholdChartStatsVisitor implements StatsVisitor
{
	
	public CpuAboveThresholdChartStatsVisitor()
	{
		this.chart =new JFreeChartFacade();
	
		this.chart.setGraphType("area");
		this.chart.setHeight(1000);
		this.chart.setWidth(1000);
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
			double [] values = dataStoreEntryCount.getRawSnapshots();
			
			
			for (int i = 0; i < values.length; i++)
			{
				
				if(values[i] >= cpuPercentThreshold)
					this.chart.plotValue(values[i], name+" "+statName, Long.valueOf(times[i]));
			}
			
			//headers.add(statName+"        "+statDescriptor.getDescription());
		
			
			//values.add(String.valueOf(dataStoreEntryCount.getSnapshotsMaximum()));
		}
		
	}//------------------------------------------------
	


	/**
	 * @return the chart
	 */
	public JFreeChartFacade getChart()
	{
		return chart;
	}
	
	private String filterTypeName = "LinuxSystemStats".toUpperCase();
	private String filterStatName = "cpuActive";
	private double cpuPercentThreshold = 50.1;

	private  final JFreeChartFacade chart ;
	
}

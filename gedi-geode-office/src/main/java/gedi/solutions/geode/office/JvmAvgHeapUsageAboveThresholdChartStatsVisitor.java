package gedi.solutions.geode.office;


import gedi.solutions.geode.operations.stats.ResourceInst;
import gedi.solutions.geode.operations.stats.ResourceType;
import gedi.solutions.geode.operations.stats.StatValue;
import nyla.solutions.office.chart.Chart;
import nyla.solutions.office.chart.JFreeChartFacade;

/**
 * <pre>
 * 
 * </pre>
 * @author Gregory Green
 *
 */
public class JvmAvgHeapUsageAboveThresholdChartStatsVisitor implements  ChartStatsVisitor
{
	
	public JvmAvgHeapUsageAboveThresholdChartStatsVisitor()
	{
		String title = "JVM AVG memory greater than "+this.percentThreshold+"% ";
		
		this.chart =new JFreeChartFacade();
		this.chart.setTitle(title);

		this.chart.setGraphType(Chart.BAR_GRAPH_TYPE);
		this.chart.setHeight(2000);
		this.chart.setWidth(2000);
		this.chart.setLegend(true);
		this.chart.setTooltips(true);
	}

	

	@Override
	public void visitResourceInst(ResourceInst resourceInst)
	{
		String name = resourceInst.getName();
		if("vmNonHeapMemoryStats".equals(name))
			return; //skip;
		
		String machineName = resourceInst.getArchive().getArchiveInfo().getMachine();
		
		ResourceType resourceType= resourceInst.getType();
		final  String filterTypeName = "VMMemoryUsageStats";
		
		boolean skip =  resourceType == null || resourceType.getName() == null || 
		(filterTypeName != null && !resourceType.getName().equals(filterTypeName));
		
		if(skip)
		{
			//System.out.println("skipping resourceType:"+resourceType+" name:"+name);
			return;
		}

		
	
		StatValue[] statValues = resourceInst.getStatValues();
		if(statValues == null)
			return;
		
			
		StatValue usedMemoryStatValue = resourceInst.getStatValue("usedMemory");	
		
		double usedMemory = usedMemoryStatValue.getSnapshotsAverage()/(1073741824);
		
	    StatValue maxMemoryStatValue = resourceInst.getStatValue("maxMemory");	
		
		double maxMemory = maxMemoryStatValue.getSnapshotsMaximum()/(1073741824);
		
		
		double percent = usedMemory/maxMemory * 100;
		
		
		if(percent < percentThreshold)
			return;
		
		this.chart.plotValue(maxMemory, "max", machineName);
		this.chart.plotValue(usedMemory, "used", machineName);
	
		
		
	}//------------------------------------------------
	


	/* (non-Javadoc)
	 * @see gedi.solutions.geode.office.ChatStatsVisitor#getChart()
	 */
	@Override
	public JFreeChartFacade getChart()
	{
		return chart;
	}//------------------------------------------------
	private  final JFreeChartFacade chart ;
	private  int percentThreshold = 50;
	
}

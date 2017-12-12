package gedi.solutions.geode.qa;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.geode.cache.RegionShortcut;
import gedi.solutions.geode.util.GemFireInspector;
import gedi.solutions.geode.util.GemFireMgmt;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.operations.Shell;
import nyla.solutions.core.operations.Shell.ProcessInfo;
import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;


public class GUnit
{
	private final static String location = Config.getProperty("gfsh_location");
	private final static String runtimeDir = Config.getProperty("runtime_location","runtime");
	
	public GUnit()
	{
	}//------------------------------------------------
	public void startCluster()
	throws Exception
	{

		
		IO.mkdir(Paths.get(runtimeDir+"/locator").toFile());
		IO.mkdir(Paths.get(runtimeDir+"/server").toFile());
		 
		Shell shell = new Shell();
		ProcessInfo pi = shell.execute(location+"/gfsh","-e","start locator  --dir=runtime/locator  --name=locator  --port=10334");
		
		System.out.println(pi.exitValue);
		System.out.println(pi.output);
		System.out.println(pi.error);
		
		pi = shell.execute(location+"/gfsh",
		"-e","start server --name=server --dir="+runtimeDir+"/server --locators=localhost[10334]"); 
		
		System.out.println(pi.exitValue);
		System.out.println("OUTPUT:"+pi.output);
		System.out.println("ERROR:"+pi.error);
	}
	
	public void createRegion(String regionName,RegionShortcut regionShortcut)
	{

		Shell shell = new Shell();
		
		 ProcessInfo pi = shell.execute(location+"/gfsh",
		 "-e","connect",
		 "-e","create region --name="+regionName+" --type="+regionShortcut);
		 
		System.out.println(pi.exitValue);
		System.out.println("OUTPUT:"+pi.output);
		System.out.println("ERROR:"+pi.error);
		
	}//------------------------------------------------
	public void shutdown()
	{
		try(JMX jmx = JMX.connect("localhost", 1099))
		{
			String[] members =  GemFireMgmt.shutDown(jmx);
			
			Debugger.println("members:"+Debugger.toString(members));
			
			GemFireMgmt.stopLocator(jmx, "locator");
			
		}
		
		try
		{
			IO.delete(Paths.get(runtimeDir+"/server").toFile());		
		}
		catch(IOException e) {Debugger.printWarn(e);} 
		
		try
		{
			IO.delete(Paths.get(runtimeDir+"/locator").toFile());	
		}
		catch(IOException e) {Debugger.printWarn(e);} 
		
	
	}//------------------------------------------------
	
	/**
	 * Wait for a given member to startup
	 * @param jmx the JMX connection
	 * @param member the member to wait for
	 */
	public void waitForMemberStart(String member, JMX jmx)
	{
		boolean isRunning =  false;
		boolean printedStartMember  = false;
		int count = 0;
		while(!isRunning)
		{
			try{ isRunning = GemFireInspector.checkMemberStatus(member,jmx); }
			catch(Exception e) {Debugger.printWarn(e);}
			
			if(!printedStartMember )
			{
				Debugger.println("Waiting for member:"+member+".  Starting member to continue. "+
			            " You can perform a gfsh status command to confirm whether the member is running");
					printedStartMember = true;
			}
			
			
			try{ delay();}catch(Exception e){}
			
			 if(count > retryCount)
			 {
				 throw new RuntimeException("member:"+member+" did not start after "
			 +retryCount+" checks with a delay of "+sleepDelay+" milliseconds");
			 }
			 
			count++;
		}
	}// --------------------------------------------------------
	/**
	 * Wait for a given member to startup
	 * @param jmx the JMX connection
	 * @param member the member to wait for
	 * @throws InterruptedException 
	 */
	public void waitForMemberStop(String member, JMX jmx) throws InterruptedException
	{
		boolean isRunning =  true;
		boolean printedStartMember  = false;
		
		int count  = 0;
		while(isRunning && count < retryCount)
		{
			isRunning = GemFireInspector.checkMemberStatus(member,jmx);
			
			if(!printedStartMember )
			{
					Debugger.println("Waiting for member:"+member+" to stop.");
					printedStartMember = true;
			}
			
			delay();
			
			count++;
		}
		
		if(isRunning)
		{
			throw new RuntimeException("member:"+member+" failed to stop after "+retryCount+
					" checks with a delay of "+sleepDelay+" milliseconds");
		}
	}// --------------------------------------------------------
	
	public static void delay() 
			throws InterruptedException
	{
		System.out.println("Sleeping for "+sleepDelay+" milliseconds");
		Thread.sleep(sleepDelay);
	}// --------------------------------------------------------
	
	

	private final static long   sleepDelay = Config.getPropertyLong(GUnit.class.getName()+".sleepDelay",1000*1); //seconds
	private final static int    retryCount = Config.getPropertyInteger(GUnit.class,"retryCount",45);
	
}

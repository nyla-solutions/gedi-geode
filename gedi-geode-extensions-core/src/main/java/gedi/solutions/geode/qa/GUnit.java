package gedi.solutions.geode.qa;

import java.nio.file.Paths;

import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.distributed.LocatorLauncher;
import gedi.solutions.geode.util.GemFireMgmt;
import nyla.solutions.core.io.IO;
import nyla.solutions.core.operations.ClassPath;
import nyla.solutions.core.operations.Shell;
import nyla.solutions.core.operations.Shell.ProcessInfo;
import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Config;
import nyla.solutions.core.util.Debugger;


public class GUnit
{
	private final static String location = Config.getProperty("gfsh_location");
	private final static String runtimeDir = Config.getProperty("runtime_location","runtime");
	private final static String locatorDir = runtimeDir+"/locator";
	private final static String serverDir = runtimeDir+"/server";
	
	public GUnit()
	{
	}//------------------------------------------------
	public void startCluster()
	throws Exception
	{

		
		IO.mkdir(Paths.get(locatorDir).toFile());
		IO.mkdir(Paths.get(serverDir).toFile());
		
		Shell locatorShell = new Shell(Paths.get(locatorDir).toFile(),
		Paths.get(locatorDir+"/locator.start.log").toFile());
		
		
		ProcessInfo p = locatorShell.execute(true,"java","-classpath", ClassPath.getClassPathText(), LocatorLauncher.class.getName(),"start", "locator");
		
		if(p.hasError() && p.error.indexOf("locator is currently online") < 0)
			throw new RuntimeException(p.error);
		
		System.out.println("OUT:"+p.output);
		System.out.println("ERROR:"+p.error);
		Thread.sleep(1000);
		
		Shell cacheServerShell = new Shell(Paths.get(serverDir).toFile(),
		Paths.get(locatorDir+"/server.start.log").toFile());
		 p = cacheServerShell.execute(true,location+"/gfsh",
		"-e","start server --name=server --dir="+runtimeDir+"/server --locators=localhost[10334]"); 
		
		 System.out.println("OUT:"+p.output);
			System.out.println("ERROR:"+p.error);
		 Thread.sleep(1000);
	}
	
	public void createRegion(String regionName,RegionShortcut regionShortcut)
	{

		Shell shell = new Shell();
		
		 ProcessInfo pi = shell.execute(location+"/gfsh",
		 "-e","connect",
		 "-e","create region --name=Test --type="+regionShortcut);
		 
		System.out.println(pi.exitValue);
		System.out.println("OUTPUT:"+pi.output);
		System.out.println("ERROR:"+pi.error);
		
	}//------------------------------------------------
	public void shutdown()
	{
		try(JMX jmx = JMX.connect("localhost", 1099))
		{
			String[] members = GemFireMgmt.shutDown(jmx);
			
			Debugger.println("members:"+Debugger.toString(members));
			
			GemFireMgmt.stopLocator(jmx, "locator");
		}
	}//------------------------------------------------
}

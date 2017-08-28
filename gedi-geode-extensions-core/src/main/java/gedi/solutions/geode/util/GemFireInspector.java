package gedi.solutions.geode.util;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.apache.geode.management.DistributedRegionMXBean;
import org.apache.geode.management.DistributedSystemMXBean;
import org.apache.geode.management.GatewayReceiverMXBean;
import org.apache.geode.management.GatewaySenderMXBean;
import org.apache.geode.management.MemberMXBean;

import gedi.solutions.geode.client.SingletonGemFireJmx;
import nyla.solutions.core.exception.CommunicationException;
import nyla.solutions.core.patterns.jmx.JMX;
import nyla.solutions.core.util.Debugger;

/**
 * Contains several read only GemFire operations
 * @author Gregory Green
 *
 */
public class GemFireInspector
{
	/**
	 * 
	 * @return the string containing the 
	 */
	public static String getPrimaryGatewaySenderMember()
	{
		String objectNamePattern = "GemFire:service=GatewaySender,gatewaySender=REMOTE,type=Member,member=*";
		
		JMX jmx = SingletonGemFireJmx.getJmx();
		Collection<ObjectName> objectNames =  jmx.searchObjectNames(objectNamePattern);
		if(objectNames == null)
			return null;
		
		GatewaySenderMXBean bean = null;
		GatewaySenderMXBean primarySender = null;
		ObjectName primaryObjectName = null;
		for (ObjectName objectName : objectNames)
		{
			
			bean = jmx.newBean(GatewaySenderMXBean.class, objectName);
			
			if(bean.isPrimary())
			{
				primarySender = bean;
				primaryObjectName = objectName;
				break;	
			}
		}
		
		if(primarySender == null)
			return null;
		
		return primaryObjectName.getKeyProperty("member");
	}// --------------------------------------------------------
	
	/**
	 * 
	 * @return list of gateway senders
	 * @throws Exception when an unknown error occurs
	 */
	public static Collection<GatewaySenderMXBean> listGatewaySenders()
	throws Exception
	{
		JMX jmx = SingletonGemFireJmx.getJmx();
		DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
				new ObjectName("GemFire:service=System,type=Distributed"));
		
		ObjectName[] objectNames =  system.listGatewaySenderObjectNames();
		
		if(objectNames == null)
			return null;
		
		GatewaySenderMXBean gatewaySender = null;
		
		ArrayList<GatewaySenderMXBean> list = new ArrayList<GatewaySenderMXBean>(objectNames.length);
		
		for (ObjectName objectName : objectNames)
		{
			gatewaySender = jmx.newBean(GatewaySenderMXBean.class, objectName);
			list.add(gatewaySender);
		}
		
		return list;
	}// --------------------------------------------------------
	public static MemberMXBean getMember(String name)
	{
		try
		{
			
			JMX jmx = SingletonGemFireJmx.getJmx();

			String pattern = "GemFire:type=Member,member="+name;
			
			Set<ObjectName> objectNames = jmx.searchObjectNames(pattern);
			
			if(objectNames == null || objectNames.isEmpty())
				return null;
			
			ObjectName serverName = new ObjectName(pattern);
			
			return jmx.newBean(MemberMXBean.class,serverName);
			
			
		}
		catch (MalformedObjectNameException e)
		{
			throw new RuntimeException("Unable to get member "+name
					+" ERROR:"+e.getMessage(),e);
		}
		
	}// --------------------------------------------------------
    static DistributedSystemMXBean getDistributedSystemMXBean()
    throws Exception
    {

    	JMX jmx = SingletonGemFireJmx.getJmx();
		DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
				new ObjectName("GemFire:service=System,type=Distributed"));
		
		
		return system;
		
    }// --------------------------------------------------------
	public static Collection<GatewayReceiverMXBean> listGatewayReceivers()
			throws Exception
			{
		
				JMX jmx = SingletonGemFireJmx.getJmx();
				DistributedSystemMXBean system = jmx.newBean(DistributedSystemMXBean.class, 
						new ObjectName("GemFire:service=System,type=Distributed"));
				
				ObjectName[] objectNames =  system.listGatewayReceiverObjectNames();
				
				if(objectNames == null)
					return null;
				
				GatewayReceiverMXBean gatewayReceiver = null;
				
				ArrayList<GatewayReceiverMXBean> list = new ArrayList<GatewayReceiverMXBean>(objectNames.length);
				
				for (ObjectName objectName : objectNames)
				{
					gatewayReceiver = jmx.newBean(GatewayReceiverMXBean.class, objectName);
					list.add(gatewayReceiver);
				}
				
				return list;
			}// --------------------------------------------------------
	/**
	 * List the unique set of host name
	 * @return set of host names
	 */
	public static Set<String> listHosts()
	{
		JMX jmx = SingletonGemFireJmx.getJmx();
		Set<ObjectName> memberObjects = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(memberObjects == null || memberObjects.isEmpty())
		{
			return null;
		}
		
		HashSet<String> hostList = new HashSet<String>(memberObjects.size());
		
		MemberMXBean  bean = null;
		for (ObjectName objectName : memberObjects)
		{
			bean = jmx.newBean(MemberMXBean.class, objectName);
			try
			{
				hostList.add(bean.getHost());
			}
			catch(UndeclaredThrowableException e)
			{
				//will not be added
			}
		}
		return hostList;
	}// --------------------------------------------------------
	/**
	 * 
	 * @return member names
	 */
	public static Collection<String> listMembers()
	
	{
		JMX jmx = SingletonGemFireJmx.getJmx();
		Set<ObjectName> memberObjects = jmx.searchObjectNames("GemFire:type=Member,member=*");
		
		if(memberObjects == null || memberObjects.isEmpty())
		{
			return null;
		}
		
		ArrayList<String> memberList = new ArrayList<String>(memberObjects.size());
		
		MemberMXBean  bean = null;
		for (ObjectName objectName : memberObjects)
		{
			bean = jmx.newBean(MemberMXBean.class, objectName);
			try
			{
				memberList.add(bean.getName());
			}
			catch(UndeclaredThrowableException e)
			{
				//will not be added
			}
		}
		
		return memberList;
	}// --------------------------------------------------------

	public static boolean checkMemberStatus(String serverName)
	{
		
			try
			{
				JMX jmx = SingletonGemFireJmx.getJmx();

				ObjectName objectName = new ObjectName("GemFire:type=Member,member="+serverName);
				
				String status = (String)jmx.invoke(objectName, "status", null, null);
				boolean isOnline = status != null && status.contains("online");
				Debugger.println("member:"+serverName+" isOnline:"+isOnline);
				return isOnline;
			}
			catch (MalformedObjectNameException e)
			{
				throw new CommunicationException(e.getMessage()+ " server="+serverName);
			}
		
	}// --------------------------------------------------------
	/**
	 * 
	 * @return ObjectName(GemFire:service=System,type=Distributed) TotalRegionEntryCount attribute
	 */
	public static long getTotalRegionEntryCount()
	{
		try
		{
			JMX jmx = SingletonGemFireJmx.getJmx();
			
			ObjectName on = new ObjectName("GemFire:service=System,type=Distributed");
			
			return jmx.getAttribute(on, "TotalRegionEntryCount");
		}
		catch (Exception e)
		{
			throw new RuntimeException("Unable to obtain TotalRegionEntryCount ERROR:"+e.getMessage(),e);
		}
	}// --------------------------------------------------------
	/**
	 * 
	 * @return the region name that do not have redundancy
	 * @throws Exception when unknown error occurs
	 */
	public static Collection<String> listRegionsWithNumBucketsWithoutRedundancy()
	throws Exception
	{
		//Get object GemFire:service=System,type=Distributed
		JMX jmx = SingletonGemFireJmx.getJmx();
		
		ObjectName objectName  = new ObjectName("GemFire:service=System,type=Distributed");
		
		//DistributedSystemMXBean distributedSystemMXBean = 
		DistributedSystemMXBean bean = jmx.newBean(DistributedSystemMXBean.class, objectName);
		
		//listDistributedRegionObjectNames
		ObjectName [] regionObjectNames = bean.listDistributedRegionObjectNames();
		
		if(regionObjectNames == null)
			return null;
		
		//com.gemstone.gemfire.management.DistributedRegionMXBean
		DistributedRegionMXBean region = null;
		
		ArrayList<String> regionNamesWithoutRedundancy = new ArrayList<String>();
		for (ObjectName regionObjectName : regionObjectNames)
		{
			region = jmx.newBean(DistributedRegionMXBean.class, regionObjectName);
			if(region.getNumBucketsWithoutRedundancy() > 0)
				regionNamesWithoutRedundancy.add(region.getName());
		}
		
		if(regionNamesWithoutRedundancy.isEmpty())
			return null;
		
		return regionNamesWithoutRedundancy;
		
	}// --------------------------------------------------------
}
# Geode Extension 

## GeodeClient API

**Setup Environment**

    export LOCATOR_HOST=localhost

    export LOCATOR_PORT=10334

If authentication is enabled
	
	export SECURITY_USERNAME=user
	export SECURITY_PASSWORD=password
    
    
**Get a Apache Geode Connection**

	GeodeClient geodeClient = GeodeClient.connect()

Get the Apache Geode client cache
	
	ClientCache cache = geodeClient.getClientCache();
		

**Get a Region**
		
	Region<String,PdxInstance> region = geodeClient.getRegion("Test"))

**Execute a Query**

    Collection<PdxInstance> collection = geodeClient.select("select * from /myregion");
 
**Get a queue continous query matches**

    Queue<PdxInstance> queue = registerCq("myQueryName","select * from /myregion")
    
    //get first recod
    PdxInstance pdxRow = queue.poll();
    
    
## GemFire Commericial Repository


See the following for instruction to down the GemFire artifacts.

	https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html
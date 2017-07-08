# Geode Extension GeodeClient API

**Setup Environment**

    export LOCATOR_HOST=localhost

    export LOCATOR_PORT=10334
    
**Get a Apache Geode Connection**

	GeodeClient geodeClient = GeodeClient.connect()
		
	//Get a the Apache Geode client cache	
	ClientCache cache = geodeClient.getClientCache();
		

**Get a Region**
		
	Region<String,PdxInstance> region = geodeClient.getRegion("Test"))



**Execute a Query**

    Collection<PdxInstance> collection = geodeClient.select("select * from /myregion");
 
**Get a queue continous query matches**

    Queue<PdxInstance> queue = registerCq("myQueryName","select * from /myregion")
    
    //get first recod
    PdxInstance pdxRow = queue.poll();
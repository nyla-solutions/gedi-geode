# Geode Extension 


Note that this API is available in the Maven Repository.

	
	<dependency>
	    <groupId>com.github.nyla-solutions</groupId>
	    <artifactId>gedi-geode-extensions-core</artifactId>
	    <version>${VERSION}</version>
	</dependency>


## GeodeClient API

**Setup Environment**

    export LOCATOR_HOST=localhost
    export LOCATOR_PORT=10334
 
 **Optional Settings**

With defaults

	export PDX_SERIALIZER_CLASS_NM=...   (default org.apache.geode.pdx.ReflectionBasedAutoSerializer)

	export POOL_PR_SINGLE_HOP_ENABLED=false
	export PDX_CLASS_PATTERN=.*
	export USE_CACHING_PROXY=false
	export PDX_READ_SERIALIZED=false
	

If authentication is enabled
	
	export SECURITY_USERNAME=user
	export SECURITY_PASSWORD=password
    

If you need to set PDX read serialize to true (default false).

    export PDX_READ_SERIALIZED=true

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
 
 ## Convert Statistics to CSV file
 
 The following will extract a single statistic type with the name "CachePerfStats"
 
 	`java gedi.solutions.geode.operations.stats.GfStatsReader /Projects/LifeSciences/Humana/analysis/DigitIT/stats/stats.gfs CachePerfStats   /Projects/stats/CachePerfStats.csv`
 
 
 To export all statistics with a file name pattern `<name>.gfs.<type>.csv` in the same directory as the stat file.
 
 	`java gedi.solutions.geode.operations.stats.GfStatsReader /Projects/analysis/DigitIT/stats/stats.gfs`


# LDAP Security Manager

See [LDAP Security manager](README_LDAP_SecurityMgr.md)

 
## GemFire Commercial Repository


See the following for instruction to down the GemFire artifacts.

	https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html
# Geode Extension 


Note that this API is available in the [Maven Repository](https://mvnrepository.com/artifact/com.github.nyla-solutions/gedi-geode-extensions-core).

	
	<dependency>
	    <groupId>com.github.nyla-solutions</groupId>
	    <artifactId>gedi-geode-extensions-core</artifactId>
	    <version>${VERSION}</version>
	</dependency>



## GeodeClient API

**Setup Environment Single locator**

    export LOCATOR_HOST=localhost
    export LOCATOR_PORT=10334
    
**Setup Environment Multiple locators**
 

 Format: host[port]`(,host[port])*`
 
 Sample:
 
	export LOCATORS=host1[port],host2[port],host2[port]
 
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
  
 **Cloud Foundry/ Pivotal Cloud Cache (PCC) Friendly**
 
 Pivotal Cloud Cache [PCC](https://docs.pivotal.io/p-cloud-cache/index.html) is [Pivotal](http://pivotal.io)'s [12-factor](https://12factor.net/) [backing service](https://12factor.net/backing-services) implementation of GemFire. GeodeClient.connect method supports automatically wiring the locators hosts, ports and security credential when the PCC service is binded a Cloud Foundry application that using this API.
 
 
 See [https://docs.pivotal.io/p-cloud-cache/using-pcc.html#bind-service](https://docs.pivotal.io/p-cloud-cache/using-pcc.html#bind-service)
 
	cf bind-service [appNAme] [pcc-service]
 
 
**SSL key/trust store management**
    
If you need SSL keystore/truststores loading via CLASSPATH for 
12 factor cloud native applications such as cloud foundry Spring Boot application
see the properties below. 

	export SSL_KEYSTORE_PASSWORD=...
	export SSL_PROTOCOLS=TLSv1.2
	export SSL_TRUSTSTORE_PASSWORD=...
	export SSL_KEYSTORE_TYPE=jks
	export SSL_CIPHERS=TLS_RSA_WITH_AES_128_GCM_SHA256
	export SSL_ENABLED_COMPONENTS=gateway,server,locator,jmx
	export SSL_REQUIRE_AUTHENTICATION=true
	export SSL_TRUSTSTORE_CLASSPATH_FILE=truststore.jks
	export SSL_KEYSTORE_CLASSPATH_FILE=keystore.jks

-------------------------------------------------

**Get a Apache Geode Connection**

	GeodeClient geodeClient = GeodeClient.connect()

Get the Apache Geode client cache
	
	ClientCache cache = geodeClient.getClientCache();
		

**Get a Region**
	
	//Does not require a client.xml or pre-registration of the region on the client
	//But, the region must exist on the server
	Region<String,PdxInstance> region = geodeClient.getRegion("Test"))

**Execute a Query**

    Collection<Object> collection = geodeClient.select("select * from /myregion");
 
**Get a queue continuous query matches**

    	Queue<Object> queue = registerCq("myQueryName","select * from /myregion")
    
    	//get first record
    	Object object = queue.poll(); //non blocking
    
    	//blocking
    	BlockingQueue<Object> queue = client.registerCq("testCq", "select * from /test");
	Object take = queue.take();
    
 **Register simple java.util.Consumer listeners for region puts/delete events**
 
		Consumer<EntryEvent<String, Object>> customer = e -> System.out.println("Put event"+e);
		client.registerAfterPut("testEventRegion", putConsumer);
		client.getRegion("testEventRegion");
 
## Convert Statistics to CSV file
 
 The following will extract a single statistic type with the name "CachePerfStats"
 
 	`java gedi.solutions.geode.operations.stats.GfStatsReader /stats/stats.gfs CachePerfStats   /Projects/stats/CachePerfStats.csv`
 
 
 To export all statistics with a file name pattern `<name>.gfs.<type>.csv` in the same directory as the stat file.
 
 	`java gedi.solutions.geode.operations.stats.GfStatsReader /Projects/analysis/DigitIT/stats/stats.gfs`

 
## GemFire Commercial Repository


See the following for instruction to down the GemFire artifacts.

	https://gemfire.docs.pivotal.io/gemfire/getting_started/installation/obtain_gemfire_maven.html

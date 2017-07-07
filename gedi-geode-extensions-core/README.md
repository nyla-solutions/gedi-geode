# Geode Extension


## Getting a Geode connection


	System.setProperty("LOCATOR_HOST", "localhost");
	System.setProperty("LOCATOR_PORT", "10334");
	
	GeodeClient geodeClient = GeodeClient.connect();
	
## Getting a CACHING_PROXY region
	
	
	Region<?,?> region = geodeClient.getRegion("Test"));
	
 # GEDI Geode REST

This is an Spring Boot based app that provides the following GemFire features 
- POST region with region key and value to put data into a region
- GET region name and key to read

When starting the application you must provide the ENVironment

- LOCATOR_HOST=**public IP address or Public Host Name**
- LOCATOR_PORT =**Locator Port**
	


# REST Region Service

The URL http://**root**/region prefix exposes a REST interface to preform READ/WRITE 
operations on a GemFire region.


# POST region

Put a  key/value entry into a given region.

**NOTE** The value is expected to be a JSON string


**Format** 

	http://<root>/region/<regionName>/<key>
	
	POST BODY
		<value>

*Example*

HTTP POST

http://**host**/region/Test/hello

RESPONSE

{ "name" : "world"}



# GET region

Get a region value based on a given key

**Format** 

	http://<root>/region/<regionName>/<key>
	
*Example*

HTTP GET

http://localhost:8080/region/Test/hello

RESPONSE

{ "name" : "world"}



	
	
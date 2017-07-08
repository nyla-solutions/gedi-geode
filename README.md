#Overview

The Grid Enterprise Data Integration (GEDI) Apache Geode/GemFire extenstions aPI.

**Projects**

| Project        | Are           | 
| ------------- |:-------------:| 
| [gedi-geode-extensions-core](https://github.com/nyla-solutions/gedi-geode/tree/master/gedi-geode-extensions-core)     | Developer friendly wrapper API(s) | 
| [gedi-geode-extensions-rest](https://github.com/nyla-solutions/gedi-geode/tree/master/gedi-geode-extensions-rest)      | Exposes a REST interface for region Read/Write Operations      |

 


#Apache Geode/GemFire Installation


start locator --name=local --enable-cluster-configuration
start server --name=server1 --use-cluster-configuration --server-port=50001

deploy --jar=/Projects/solutions/nyla/dev/nyla.solutions.core/build/libs/nyla.solutions.core-0.3-SNAPSHOT.jar

deploy --jar=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-0.0.1-SNAPSHOT.jar



    
 
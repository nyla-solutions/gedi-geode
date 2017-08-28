# Overview

The Grid Enterprise Data Integration (GEDI) Apache Geode/GemFire extenstions aPI.

**Projects**

| Project        | Are           | 
| ------------- |:-------------:| 
| [gedi-geode-extensions-core](https://github.com/nyla-solutions/gedi-geode/tree/master/gedi-geode-extensions-core)     | Developer friendly wrapper API(s) | 
| [gedi-geode-extensions-rest](https://github.com/nyla-solutions/gedi-geode/tree/master/gedi-geode-extensions-rest)      | Exposes a REST interface for region Read/Write Operations      |
| [gedi-geode-extensions-int-test](https://github.com/nyla-solutions/gedi-geode/tree/master/gedi-geode-extensions-int-test)      | Integration test cases      |

 


#Apache Geode/GemFire Installation


start locator --name=local --enable-cluster-configuration
start server --name=server1 --use-cluster-configuration --server-port=50001

deploy --jar=/Projects/solutions/nyla/dev/nyla.solutions.core/build/libs/nyla.solutions.core-VERSION.jar

deploy --jar=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-VERSION.jar


    
 
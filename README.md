#Installation


start locator --name=local --enable-cluster-configuration
start server --name=server1 --use-cluster-configuration --server-port=50001

deploy --jar=/Projects/solutions/nyla/dev/nyla.solutions.core/build/libs/nyla.solutions.core-0.3-SNAPSHOT.jar

deploy --jar=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-0.0.1-SNAPSHOT.jar


# Projects


- [gedi-geode-extensions-core](https://github.com/nyla-solutions/gedi-geode) contains extension API(s) for common geode addon needs



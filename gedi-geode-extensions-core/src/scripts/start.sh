export GEMFIRE_HOME=/devtools/repositories/IMDG/pivotal-gemfire-9.0.4

mkdir -p /Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/local
mkdir -p /Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/server1
mkdir -p /Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/server2

$GEMFIRE_HOME/bin/gfsh -e "start locator --name=local --dir=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/local  --enable-cluster-configuration"

$GEMFIRE_HOME/bin/gfsh -e "start server --locators=localhost[10334] --server-port=10100 --name=server1 --use-cluster-configuration --dir=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/server1"&
$GEMFIRE_HOME/bin/gfsh -e "start server --locators=localhost[10334]  --server-port=10101 --name=server2 --use-cluster-configuration --dir=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/runtime/server2"
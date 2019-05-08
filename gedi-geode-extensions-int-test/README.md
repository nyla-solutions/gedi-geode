

cp /Projects/solutions/nyla/dev/nyla.solutions.core/build/libs/nyla.solutions.core-1.2.1.jar /Projects/Pivotal/dataEng/dev/gemfire-devOps/lib/

connect --locator=MacBook-Pro-5.local[10334]


gfsh>deploy --jar=/Projects/solutions/nyla/dev/nyla.solutions.core/build/libs/nyla.solutions.core-1.2.1-SNAPSHOT.jar

gfsh>deploy --jar=/Projects/solutions/gedi/dev/gedi-geode/gedi-geode-extensions-core/target/gedi-geode-extensions-core-1.2.1.jar


gfsh>execute function --id=ExportCsvFunction --region=/testCsv --args



create region --name=testCsv --type=PARTITION

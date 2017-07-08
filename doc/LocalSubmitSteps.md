##Steps to submit a spark job locally
1. Make sure /etc/hosts has an entry for your `hostname` as "127.0.0.1"
 - Or temporarily change your hostname by `sudo hostname -s 127.0.0.1`

2. ```bash 
spark-submit --class com.chen.guo.example.WordCount \
--conf spark.chen.guo.word.count.filepath=file:///Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/src/main/resources/com/chen/guo/example/WordCountExampleFile.txt \
file:///Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/build/libs/spark-pipeline-framework-1.0-SNAPSHOT.jar```

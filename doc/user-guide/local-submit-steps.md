##Steps to submit a spark job locally
1. Make sure /etc/hosts has an entry for your `hostname` as "127.0.0.1"
 - Or temporarily change your hostname by `sudo hostname -s 127.0.0.1`

2. Submit the spark job via spark-submit

  ```bash
  spark-submit --class com.chen.guo.example.WordCount \
--conf spark.chen.guo.word.count.filepath=file:///Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/spark-ingestion/src/main/resources/com/chen/guo/example/WordCountExampleFile.txt \
file:///Users/chguo/repos/enjoyear/Spark-Pipeline-Framework/spark-ingestion/build/libs/spark-ingestion.jar
  ```
3. 



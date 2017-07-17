#!/usr/bin/env bash
BIN_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && cd .. && pwd )"

if [ ! -f ${BIN_DIR}/jars/avro-tools-1.8.1.jar ]; then
   cd $BIN_DIR/jars/ && curl -O http://central.maven.org/maven2/org/apache/avro/avro-tools/1.8.1/avro-tools-1.8.1.jar
fi

java -jar ${BIN_DIR}/jars/avro-tools-1.8.1.jar tojson --pretty $1

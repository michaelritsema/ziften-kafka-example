## Ziften Kafka Example
#### This app is an example on how to consume Ziften Protocolbuffers straight from Ziften's Kafka topic.

##### Running the example
A prebuilt jar is in /build
Run from command line:
  
    $ java -jar build/ziften-kafka-example.jar ec2-54-161-12-134.compute.amazonaws.com:9092 ZIFTEN.DATACOLLECTION_
  
  * First parameter is the Kafka server string: remote_host:9092
  * Second paramter is the topic name: ZIFTEN.DATACOLLECTION_
  
In some installations the topic name could be different. Contact Ziften to find out the exact name.
  
##### Building and using the protocol buffers library and Ziften's helper functions

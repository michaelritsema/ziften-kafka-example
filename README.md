## Ziften Kafka Example
#### This app is an example on how to consume Ziften Protocolbuffers straight from Ziften's Kafka topic.

##### Running the example
A prebuilt Java 8 jar is in /build

Run from command line:
  
    $ java -jar build/ziften-kafka-example.jar ec2-54-161-12-134.compute.amazonaws.com:9092 ZIFTEN.DATACOLLECTION_
  
  * First parameter is the Kafka server string: remote_host:9092
  * Second paramter is the topic name: ZIFTEN.DATACOLLECTION_
  
In some installations the topic name could be different. Contact Ziften to find out the exact name.
  
##### Building and using the protocol buffers library and Ziften's helper functions
The Ziften Topic has messages froms in this format:


    <pb type="MessageType" hmac="">BASE64==</pb>
 
 Each type is associated with a protobuf message. The text of the pb element is a base64 encoded protobuf. You can use the type to determine what protobuf message you need to use to decode the message. Some helper utilties were provided. 
 
 
     String xmlMSg=" <pb type="MessageType" hmac="">BASE64aaabbbccccc==</pb>"
     AbstractMessage abstractMessage = new ProtobufMessageUtil().decode(xmlMsg);
     System.out.printf("\n\nFound protobuf message: %s", abstractMessage.getClass().getSimpleName());
     System.out.println("\n\t" + ProtobufToJSON.toJSONObjectModel(abstractMessage));  

* Please note that ProtobufTOJSON now actually returns a Map<String,Object>. 

A jar containing just the protobuffer messages is at:
  - /protocol-0.0.2-SNAPSHOT.jar

An example of consuming off a Kafka topic is also provided. Ziften uses Kafka 0.10. The consumer was refactored in Kafka 0.9, so please consult the 0.10 documentaiton if you are not familiar: https://kafka.apache.org/documentation 

To build the whole project:
    mvn installl
 
This should result in a new shaded jar at target/kafka-example-jar
At this point you run it the same way as the provided example jar:


        $ java -jar build/ziften-kafka-example.jar ec2-54-161-12-134.compute.amazonaws.com:9092 ZIFTEN.DATACOLLECTION_
  

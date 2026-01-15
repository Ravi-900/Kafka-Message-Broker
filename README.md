# Kafka
> Distributed event streaming platform for building real-time data pipelines and streaming applications, handling massive volumes of data with high throughput and low latency.

It acts as a publish-subscribe messaging system that allows applications to **send (publish)** and **receive (subscribe)** **streams of data (event)** in real time, storing them reliably in **topics  (categories)**.
### **Why Kafka**
- Distributed, resilient architecture, fault tolerant
- Horizontal scalability:
	- Can scale to 100s brokers
	- Can scale to millions of messages per second
- High performance (latency of less than 10ms) - real time
### **Kafka - Use Cases**
- Messaging System
- Activity Tracking
- Gather metrics from many different locations
- Application logs gathering
- Stream processing (with the Kafka Streams API for example)
- De-coupling of system dependencies
- Integration with Spark, Flink, Storm, Hadoop, and many other Big Data technologies
- Micro-services pub/sub
### **Kafka - Examples**
- **Netflix** uses Kafka to apply recommendations in real-time while you're watching TV shows
- **Uber** uses Kafka to gather user, taxi, and trip data in real-time to compute and forecast demand, and compute surge pricing in real-time
- **LinkedIn** uses Kafka to prevent spam, collect user interactions to make better connection recommendations in real time.
### **Kafka Topics**
- **Topics:** a particular stream of data
	- Categories used to organize and store event streams.
- Like a table in a database (without all the constraints)
- You can have as many topics as you want
- A topic is identified by its **name**
- These topics support any kind of message format - JSON,XML, etc
- The sequence of messages is called a **data stream.**
- You cannot query topics, instead, use Kafka Producers to send data and Kafka Consumers to read the data
### **Partitions and Offsets**
- Topics are split in partitions (example: 100 partitions)
	- Messages within each partition are ordered
	- Each message within a partition gets an incremental id, called offset
- Kafka topics are immutable: once data is written to a partition, it cannot be changed
### **Topics, Partitions and Offsets - important notes**
- Once the data is written to a partition, it cannot be changed (immutability)
- Data is kept only for a limited time (default is one week - configurable)
- Offset only have a meaning for a specific partition.
	- E.g. offset 3 in partition 0 doesn't represent the same data as offset 3 in partition 1.
	- Offsets are not re-used even if previous messages have been deleted
- Order is guaranteed only within a partition (not across partitions)
- Data is assigned randomly to a partition unless a key is provided.
- You can have as many partitions per topic as you want
### **Producers**
- Producers write data to topics (which are made of partitions)
- Producers know to which partition to write to (and which Kafka broker has it)
- In case of Kafka broker failures, Producers will automatically recover
	- load is balanced to many brokers thanks to the number of partitions
### **Producers: Message Keys**
- Producers can choose to send a key with the message (string, number, binary, etc..)
- If key=null, data is sent round robin (partition 0, then 1, then 2...)
- If key != null, then all messages for the that key will always go to the same partition (hashing)
- A key are typically sent if you need message ordering for a specific field
#### **Kafka Messages Structure**
>Kafka Message Created by the Producer

#### **Kafka Message Serializer**
- Kafka only accepts bytes as an input from producers and sends bytes out as an output to consumers.
- Message Serialization means transforming objects / data into bytes
- They are used on the value and the key
- Common Serializers
	- String
	- Int, Float
	- Avro
	- Protobuf
### **Consumers**
- Consumers read data from a topic (identified by name) - pull model
- Consumers automatically know which broker to read from
- In case of broker failures, consumers know how recover
- Data is read on order from low to high offset within each partitions
#### **Consumer Deserializer**
- Deserialize indicates how to transform bytes into objects / data
- They are used on the value and the key of the message
- Common Deserializers
	- String
	- Int, Float
	- Avro
	- Protobuf
- The serialization / Deserialization type must not change during a topic lifecycle (create a new topic instead
### **Consumer Groups**
- All the consumers in an application read data as a consumer groups
- Each consumer within a group reads from exclusive partitions
- If you have more consumers than partitions, some consumers will be inactive
- In Kafka it is acceptable to have multiple consumer groups on the same topic
### **Consumer Offsets**
- Kafka stores the offsets at which a consumer group has been reading
- The offsets committed are in Kafka topic named **__consumer_offsets**
- When a consumer in a group has processed data received from Kafka, it should be periodically committing the offsets (the Kafka broker will write to **__consumer_offsets**, not the group itself)
- If a consumer dies, it will be able to read back from where it left off thanks to the committed consumer offsets!
#### **Delivery Semantics for Consumers**
- By default, Java Consumers will automatically commit offsets (at least once)
- There are 3 delivery semantics if you choose to commit manually
- **At least once (usually preferred)**
	- Offsets are committed after the message is processed
	- If the processing goes wrong, the message will be read again
	- This can result in duplicate processing of messages. Make sure your processing is idempotent (i.e. processing again the message won't impact your systems)
- **At most once**
	- Offsets are committed as soon as messages are received
	- If the processing goes wrong, some messages will be lost (they won't be read again)
- **Exactly once**
	- For Kafka => Kafka workflows: use the transactional API
	- For Kafka => External System workflows: use an idempotent consumer
### **Kafka Brokers**
- A Kafka cluster is composed of multiple brokers (Servers)
- Each broker is identified with its ID (integer)
- Each broker contains certain topic partitions
- After connecting to any broker (called a bootstrap broker), you will be connected to the entire cluster (Kafka clients have smart mechanics for that)
- A good number to get started is 3 broker, but some big clusters have over 100 brokers
### **Kafka Broker Discovery**
- Every Kafka broker is also called a **bootstrap server**
- That means that you only need to connect to one broker, and the Kafka clients will know how to be connected to the entire cluster (smart clients)
- Each broker knows about all brokers, topics and partitions (metadata)
### **Topic Replication Factor**
- Topics should have a replication factor > 1 (usually between 2 and 3)
- This way if a broker is down, another broker can serve the data
- Example: Topic-A with 2 partitions and replication factor of 2
### **Concept of Leader for a Partition**
- At any time only one broker can be a leader for a given partition
- Producers can only send data to the broker that is leader of a partition
- The other brokers will replicate the data
- Therefore, each partition has one leader and multiple ISR (in-sync replica)
### **Default Producer & Consumer Behaviour with Leaders**
- Kafka Producers can only write to the leader broker for a partition
- Kafka Consumers by default will read from the leader broker for a partition
### **Zookeeper**
- Zookeeper manages brokers (keeps a list of them)
- Zookeeper helps in performing leader election for partitions
- Zookeeper sends notification to Kafka in case of changes (e.g. new topic, broker dies, broker comes up, delete topics, etc...)
### **Process to Start the Kafka Server**
```Java
// got to kafka directory
:~$ cd kafka

// create a cluster id
:~/kafka$ bin/kafka-storage.sh random-uuid
ZkHgbnrpRNe6mWBHc7nMLg

// Format Kafka Storage (one-time)
:~/kafka$ kafka-storage.sh format --cluster-id ZkHgbnrpRNe6mWBHc7nMLg --config config/server.properties

// Start Kafka Broker
:~/kafka$ bin/kafka-server-start.sh config/server.properties
```
### **Create a Kafka Topic**
```Java
:~/kafka$ bin/kafka-topics.sh --create --topic driver-location-updates --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
```
### **List all Kafka Topics**
```Java
:~/kafka$ bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
### **Describe a Kafka Topic**
```Java
:~/kafka$ bin/kafka-topics.sh --describe --topic driver-location-updates  --bootstrap-server localhost:9092
```
### **Delete a Kafka Topic**
```Java
:~/kafka$ bin/kafka-topics.sh --delete --topic driver-location-updates --bootstrap-server localhost:9092
```


```Java
# Replace "kafka-topics.sh"

# by "kafka-topics" or "kafka-topics.bat" based on your system # (or bin/kafka-topics.sh or bin\windows\kafka-topics.bat if you didn't setup PATH / Environment variables)

############################
#####     LOCALHOST    #####
############################
kafka-topics.sh
kafka-topics.sh --bootstrap-server localhost:9092 --list

kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --create

kafka-topics.sh --bootstrap-server localhost:9092 --topic second_topic --create --partitions 3

kafka-topics.sh --bootstrap-server localhost:9092 --topic third_topic --create --partitions 3 --replication-factor 2

# Create a topic (working)
kafka-topics.sh --bootstrap-server localhost:9092 --topic third_topic --create --partitions 3 --replication-factor 1

# List topics
kafka-topics.sh --bootstrap-server localhost:9092 --list

# Describe a topi
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --describe

# Delete a topic
kafka-topics.sh --bootstrap-server localhost:9092 --topic first_topic --delete
# (only works if delete.topic.enable=true)
```

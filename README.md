# Cache me if you can

## Pre-requisites
- Atlas MongoDB account with a free tier cluster and sample data load.
- Confluent Cloud account with a kafka queue.
- Atlas Stream Processor instance.

## Step by step

### Simple CRUD REST API with MongoDB

1. [install quarkus cli](https://quarkus.io/guides/cli-tooling).
2. Set up a new Quarkus project using the Quarkus CLI.
``` shell 
quarkus create app ifg:mug-movies --extension='quarkus-mongodb-rest-data-panache,quarkus-rest,quarkus-rest-jackson' --no-code
```
3. Set up the MongoDB connection in `src/main/resources/application.properties`:
``` properties
%dev.quarkus.mongodb.connection-string=mongodb+srv://mugcluster0.8zlaiur.mongodb.net
quarkus.mongodb.database=sample_mflix

%dev.quarkus.mongodb.tls=true
%dev.quarkus.mongodb.tls-configuration-name=mongo
%dev.quarkus.mongodb.credentials.auth-mechanism=MONGODB-X509

%dev.quarkus.tls.mongo.key-store.pem.key-certs.key=.tls/mongo-key.pem
%dev.quarkus.tls.mongo.key-store.pem.key-certs.cert=.tls/mongo-cert.pem
```
Note: The `mongo-key.pem` and `mongo-cert.pem` files can be obtained from your Atlas UI and should be placed in the `.tls` directory at the root of your project.
4. Create a new entity class `Movie.java` in `src/main/java/ifg/mug/movies/entity` and define the fields you want to expose.
``` java
@MongoEntity(collection = "movies")
public class Movie extends PanacheMongoEntity {
    // Define your fields here
}
```
6. Create PanacheMongoEntityResource class `MovieResource.java` in `src/main/java/ifg/mug/movies/rest`:
``` java
public interface MovieResource extends PanacheMongoEntityResource<Movie, ObjectId> {
}
```
6. Run the application in dev mode:
``` shell
./mvnw quarkus:dev
```
7. Test the API.

### Measure performance with oha

1. Install [oha (おはよう)](https://github.com/hatoo/oha?tab=readme-ov-file#download-pre-built-binary).
2. Run the load test:
``` shell
oha -z 10s -c 20 -q 50 --latency-correction --disable-keepalive 'http://localhost:8080/movie?title=The%20Four%20Horsemen%20of%20the%20Apocalypse'
```

### Introduce Cache

1. Add the `quarkus-cache` extension to your project:
``` shell
./mvnw quarkus:add-extension -Dextensions="quarkus-cache"
```
2. Introduce caching in your `MovieResource.java`:
``` java
    @GET
    @Path("/{id}/cached")
    @Produces("application/json")
    @CacheResult(cacheName = "movieCache")
    default Movie findByIdCached(@PathParam("id") String id) {
        Log.debugf("Find movie by id: %s", id);
        return Movie.findById(new ObjectId(id));
    }
```
3. Repeat the load test with caching enabled:
``` shell
oha -z 10s -c 20 -q 50 --latency-correction --disable-keepalive 'http://localhost:8080/movie/5f8d0b2f4d3e2c001f8b4567/cached'
```

### Introduce cache invalidation with Kafka
1. Set Atlas Stream Processor to listen to the `movie` collection and produce events to a Kafka topic.
2. Add the `quarkus-kafka-client` extension to your project:
``` shell
./mvnw quarkus:add-extension -Dextensions="quarkus-kafka-client"
```
3. Configure the Kafka connection in `src/main/resources/application.properties`:
``` properties
%dev.kafka.bootstrap.servers=${ENV_KAFKA_BOOTSTRAP_SERVERS}
kafka.group.id=mug-movies
kafka.key.deserializer=org.apache.kafka.common.serialization.StringDeserializer
kafka.value.deserializer=org.apache.kafka.common.serialization.StringDeserializer
%dev.kafka.security.protocol=SASL_SSL
%dev.kafka.sasl.mechanism=PLAIN
%dev.kafka.sasl.jaas.config=org.apache.kafka.common.security.plain.PlainLoginModule required username='${ENV_KAFKA_CLUSTER_API_KEY}' password='${ENV_KAFKA_CLUSTER_API_SECRET}';

mp.messaging.incoming.mug-movies-cdc.topic=mug-movies-cdc
mp.messaging.incoming.mug-movies-cdc.connector=smallrye-kafka
mp.messaging.incoming.mug-movies-cdc.offset.auto.reset=earliest
```
Note: Create `.env` file in the root of your project with the following content:
``` properties
ENV_KAFKA_BOOTSTRAP_SERVERS=YOUR_URL_HERE.aws.confluent.cloud:9092
ENV_KAFKA_CLUSTER_API_KEY=INSERT_YOUR_KAFKA_CLUSTER_API_KEY_HERE
ENV_KAFKA_CLUSTER_API_SECRET=INSERT_YOUR_KAFKA_CLUSTER_API_SECRET_HERE
```
3. Create a Kafka consumer `MovieCdcConsumer.java`:
``` java
    @Incoming("mug-movies-cdc")
    public void consume(ConsumerRecord<String, String> record) {
        Log.debugf("Received record: %s", record);
        invalidateCache(record.key());
    }

    @CacheInvalidate(cacheName = "movieCache")
    public void invalidateCache(String id) {
        Log.debugf("Cache invalidated for movie with id: %s", id);
    }
```
4. Set logging level to DEBUG in `src/main/resources/application.properties`:
``` properties
quarkus.log.category."ifg".level=DEBUG
```
5. Verify that the cache is invalidated when a movie is updated in the MongoDB collection. You can do this by updating a movie in the MongoDB Atlas UI and checking the logs for cache invalidation messages.
6. Well done! You have successfully implemented a simple CRUD REST API with MongoDB, measured its performance, introduced caching, and set up cache invalidation using Kafka.
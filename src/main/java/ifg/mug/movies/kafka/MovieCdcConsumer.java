package ifg.mug.movies.kafka;

import jakarta.enterprise.context.ApplicationScoped;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.logging.Log;

@ApplicationScoped
public class MovieCdcConsumer {

    @Incoming("mug-movies-cdc")
    public void consume(ConsumerRecord<String, String> record) {
        Log.debugf("Received record: %s", record);
        invalidateCache(record.key());
    }

    @CacheInvalidate(cacheName = "movieCache")
    public void invalidateCache(String id) {
        Log.debugf("Cache invalidated for movie with id: %s", id);
    }
}

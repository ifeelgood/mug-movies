package ifg.mug.movies.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.bson.types.ObjectId;

import ifg.mug.movies.mongo.Movie;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import io.quarkus.mongodb.rest.data.panache.PanacheMongoEntityResource;

public interface MovieResource extends PanacheMongoEntityResource<Movie, ObjectId> {
    //    Map<ObjectId, Movie> cache = new ConcurrentHashMap<>();

    @GET
    @Path("/cached/{id}")
    @Produces("application/json")
    @CacheResult(cacheName = "movieCache")
    default Movie findByIdCached(@PathParam("id") ObjectId id) {
        return Movie.findById(id);
    }

    @CacheInvalidate(cacheName = "movieCache")
    default void invalidateCache(ObjectId id) {
    }
}

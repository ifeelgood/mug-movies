package ifg.mug.movies.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import org.bson.types.ObjectId;

import ifg.mug.movies.mongo.Movie;

import io.quarkus.cache.CacheResult;
import io.quarkus.logging.Log;
import io.quarkus.mongodb.rest.data.panache.PanacheMongoEntityResource;

public interface MovieResource extends PanacheMongoEntityResource<Movie, ObjectId> {

    @GET
    @Path("/{id}/cached")
    @Produces("application/json")
    @CacheResult(cacheName = "movieCache")
    default Movie findByIdCached(@PathParam("id") String id) {
        Log.debugf("Find movie by id: %s", id);
        return Movie.findById(new ObjectId(id));
    }
}

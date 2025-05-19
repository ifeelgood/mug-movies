package ifg;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import ifg.mug.movies.mongo.documents.Movie;

@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        Movie movie = Movie.find("title", "The Great Train Robbery").firstResult();
        return "Hello from Quarkus REST. A movie is found: " + movie.title;
    }
}

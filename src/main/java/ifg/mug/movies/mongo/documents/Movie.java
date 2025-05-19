package ifg.mug.movies.mongo.documents;

import java.util.Date;
import java.util.List;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "movies")
public class Movie extends PanacheMongoEntity {

    public String plot;
    public List<String> genres;
    public int runtime;
    public List<String> cast;
    public String poster;
    public String title;
    public String fullplot;
    public List<String> languages;
    public Date released;
    public List<String> directors;
    public String rated;
    public Awards awards;
    public String lastupdated;
    public int year;
    public Imdb imdb;
    public List<String> countries;
    public String type;
    public Tomatoes tomatoes;
    public int num_mflix_comments;

    public static class Awards {
        public int wins;
        public int nominations;
        public String text;
    }

    public static class Imdb {
        public double rating;
        public int votes;
        public int id;
    }

    public static class Tomatoes {
        public Viewer viewer;
        public int fresh;
        public Critic critic;
        public int rotten;
        public Date lastUpdated;

        public static class Viewer {
            public double rating;
            public int numReviews;
            public int meter;
        }

        public static class Critic {
            public double rating;
            public int numReviews;
            public int meter;
        }
    }
}

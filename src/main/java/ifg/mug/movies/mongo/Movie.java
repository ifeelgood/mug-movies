package ifg.mug.movies.mongo;

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
    public int num_mflix_comments;
    public Imdb imdb;
    public List<String> countries;
    public String type;
    public Tomatoes tomatoes;
    public List<String> writers;
    public Integer metacritic;
    public int year;

    public static class Awards {
        public int wins;
        public int nominations;
        public String text;
    }

    public static class Imdb {
        public int id;
        public Double rating;
        public Integer votes;
    }

    public static class Tomatoes {
        public String boxOffice;
        public String consensus;
        public Critic critic;
        public Date dvd;
        public int fresh;
        public Date lastUpdated;
        public String production;
        public int rotten;
        public Viewer viewer;
        public String website;

        public static class Critic {
            public Integer meter;
            public int numReviews;
            public double rating;
        }

        public static class Viewer {
            public Integer meter;
            public int numReviews;
            public double rating;
        }
    }
}
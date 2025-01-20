package org.example.models;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class MovieProcessor implements Runnable {
    private final MovieDetails movieDetails;
    private final ImdbApi imdbapi;

    public MovieProcessor(String movie, ImdbApi imdbapi) {
        this.movieDetails = new MovieDetails();
        this.movieDetails.initializeMovie(movie);
        this.imdbapi = imdbapi;
    }

    @Override
    public void run() {
        System.out.println("Processing movie: " + movieDetails.getImdbId() +
                " on thread: " + Thread.currentThread().getName());
        // TODO: have to create a set for IMDB rating and rating count, and check for null
        System.out.println(imdbapi.getRating(movieDetails.getImdbId()));
    }

    public MovieDetails getMovieDetails() {
        return movieDetails;
    }
}

package org.example.models;


import static org.example.models.HelperFunctions.extractImdbRating;

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

        String jsonRating = imdbapi.getRating(movieDetails.getImdbId());
        ImdbRating imdbRating = extractImdbRating(jsonRating);
        movieDetails.setImdbRating(imdbRating.getRating());
        System.out.println(imdbRating.getRating());
        movieDetails.setImdbRatingCount(imdbRating.getRatingCount());
        System.out.println(imdbRating.getRatingCount());
    }

    public MovieDetails getMovieDetails() {
        return movieDetails;
    }
}

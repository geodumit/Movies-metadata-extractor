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
        String jsonRating = imdbapi.getRating(movieDetails.getImdbId());
        ImdbRating imdbRating = extractImdbRating(jsonRating);
        movieDetails.setImdbRating(imdbRating.getRating());
        movieDetails.setImdbRatingCount(Long.toString(HelperFunctions.convertShorthandToNumber(imdbRating.getRatingCount())));
    }

    public MovieDetails getMovieDetails() {
        return movieDetails;
    }
}

package org.example.models;

public class ImdbRating {
    private final String rating;
    private final String ratingCount;

    public ImdbRating(String rating, String ratingCount) {
        this.rating = rating;
        this.ratingCount = ratingCount;
    }

    public String getRating() {
        return rating;
    }

    public String getRatingCount() {
        return ratingCount;
    }
}

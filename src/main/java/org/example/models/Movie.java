package org.example.models;

public class Movie {
    private boolean adult;
    private int id;
    private String original_title;
    private double popularity;
    private boolean video;

    public double getPopularity() {
        return popularity;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + original_title + '\'' +
                ", popularity=" + popularity +
                ", adult=" + adult +
                ", video=" + video +
                '}';
    }
}

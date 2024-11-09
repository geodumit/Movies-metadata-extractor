package org.example.models;

import org.json.JSONObject;

public class MovieCredits {
    private String id;
    private String cast;
    private String crew;

    public String getId() {
        return id;
    }

    public String getCast() {
        return cast;
    }

    public String getCrew() {
        return crew;
    }

    public void initializeMovie(String jsonString){
        JSONObject movie = new JSONObject(jsonString);

        id = checkInt(movie,"id");
        cast = checkJSONArray(movie, "cast");
        crew = checkJSONArray(movie, "crew");
    }

    private String checkInt(JSONObject movie, String key) {
        if (movie.isNull(key)) {
            return null;
        } else {
            return String.valueOf(movie.getInt(key));
        }
    }

    private String checkJSONArray(JSONObject movie, String key) {
        if (movie.isNull(key)) {
            return null;
        } else {
            return String.valueOf(movie.getJSONArray(key));
        }
    }
}

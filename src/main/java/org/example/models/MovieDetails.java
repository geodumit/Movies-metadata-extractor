package org.example.models;

import org.json.JSONObject;

public class MovieDetails {
    String adult;
    String backdropPath;
    String collection;
    String budget;
    String genres;
    String homepage;
    String id;
    String imdbId;
    String originCountry;
    String originalLanguage;
    String originalTitle;
    String overview;
    String popularity;
    String posterPath;
    String productionCompanies;
    String productionCountries;
    String releaseDate;
    String revenue;
    String runtime;
    String spokenLanguages;
    String status;
    String tagline;
    String title;
    String video;
    String voteAverage;
    String vote_count;

    public String getAdult() {
        return adult;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getCollection() {
        return collection;
    }

    public String getBudget() {
        return budget;
    }

    public String getGenres() {
        return genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getId() {
        return id;
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getProductionCompanies() {
        return productionCompanies;
    }

    public String getProductionCountries() {
        return productionCountries;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getRevenue() {
        return revenue;
    }

    public String getRuntime() {
        return runtime;
    }

    public String getSpokenLanguages() {
        return spokenLanguages;
    }

    public String getStatus() {
        return status;
    }

    public String getTagline() {
        return tagline;
    }

    public String getTitle() {
        return title;
    }

    public String getVideo() {
        return video;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public String getVote_count() {
        return vote_count;
    }

    public void initializeMovie(String jsonString) {
        JSONObject movie = new JSONObject(jsonString);
        adult = checkBoolean(movie, "adult");
        backdropPath = checkString(movie, "backdrop_path");
        collection = checkJSONObject(movie, "belongs_to_collection");
        budget = checkInt(movie, "budget");
        genres = checkJSONArray(movie, "genres");
        homepage = checkString(movie, "homepage");
        id = checkInt(movie,"id");
        imdbId = checkString(movie,"imdb_id");
        originCountry = checkJSONArray(movie,"origin_country");
        originalLanguage = checkString(movie,"original_language");
        originalTitle = checkString(movie, "original_title");
        overview = checkString(movie,"overview");
        popularity = checkDouble(movie,"popularity");
        posterPath = checkString(movie,"poster_path");
        productionCompanies = checkJSONArray(movie, "production_companies");
        productionCountries = checkJSONArray(movie,"production_countries");
        releaseDate = checkString(movie,"release_date");
        revenue = checkInt(movie,"revenue");
        runtime = checkInt(movie,"runtime");
        spokenLanguages = checkJSONArray(movie,"spoken_languages");
        status = checkString(movie,"status");
        tagline = checkString(movie,"tagline");
        title = checkString(movie,"title");
        video = checkBoolean(movie,"video");
        voteAverage = checkDouble(movie,"vote_average");
        vote_count = checkInt(movie, "vote_count");
    }

    private String checkString(JSONObject movie, String key){
        if (movie.isNull(key)){
            return null;
        } else {
            return movie.getString(key);
        }
    }

    private String checkJSONObject(JSONObject movie, String key){
        if (movie.isNull(key)) {
            return null;
        } else {
            return String.valueOf(movie.getJSONObject(key));
        }
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

    private String checkDouble(JSONObject movie, String key) {
        if (movie.isNull(key)) {
            return null;
        } else {
            return String.valueOf(movie.getDouble(key));
        }
    }

    private String checkBoolean(JSONObject movie, String key) {
        if (movie.isNull(key)){
            return null;
        } else {
            return String.valueOf(movie.getBoolean(key));
        }
    }

    public static String getColumns(){
        return "adult;"+
                "backdropPath;"+
                "collection;"+
                "budget;"+
                "genres;"+
                "homepage;"+
                "id;"+
                "imdbId;"+
                "originCountry;"+
                "originalLanguage;"+
                "originalTitle;"+
                "overview;"+
                "popularity;"+
                "posterPath;"+
                "productionCompanies;"+
                "productionCountries;"+
                "releaseDate;"+
                "revenue;"+
                "runtime;"+
                "spokenLanguages;"+
                "status;"+
                "tagline;"+
                "title;"+
                "video;"+
                "voteAverage;"+
                "vote_count;";
    }

    public String getCSVRow(){
        return adult + ";" +
                backdropPath + ";" +
                collection + ";" +
                budget + ";" +
                genres + ";" +
                homepage + ";" +
                id + ";" +
                imdbId + ";" +
                originCountry + ";" +
                originalLanguage + ";" +
                originalTitle + ";" +
                overview + ";" +
                popularity + ";" +
                posterPath + ";" +
                productionCompanies + ";" +
                productionCountries + ";" +
                releaseDate + ";" +
                revenue + ";" +
                runtime + ";" +
                spokenLanguages + ";" +
                status + ";" +
                tagline + ";" +
                title + ";" +
                video + ";" +
                voteAverage + ";" +
                vote_count;

    }
}

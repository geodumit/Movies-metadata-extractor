package org.example.strings;

import java.util.*;

public class DatabaseData {
    // TODO: Get the column names from the db-init/create_tables.sql file
    static String[] detailsColumns = {
            "adult", "backdropPath", "collection", "budget", "genres", "homepage",
            "id", "imdbId", "originCountry", "originalLanguage", "originalTitle",
            "overview", "popularity", "posterPath", "productionCompanies",
            "productionCountries", "releaseDate", "revenue", "runtime",
            "spokenLanguages", "status", "tagline", "title", "video",
            "voteAverage", "vote_count", "imdbRating", "imdbRatingCount"
    };

    static String[] creditsColumns = {
            "id","_cast", "crew"
    };

    static String detailsTable = "raw_details_metadata";
    static String creditsTable = "raw_credits_metadata";


    static public String[] getDetailsColumns() {
        return detailsColumns;
    }

    static public String[] getCreditsColumns() {
        return creditsColumns;
    }

    public static String getDetailsTable() {
        return detailsTable;
    }

    public static String getCreditsTable() {
        return creditsTable;
    }
}

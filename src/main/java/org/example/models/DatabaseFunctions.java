package org.example.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseFunctions {
    private static final String URL = "jdbc:postgresql://localhost:5432/moviesMetadata";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password";


    public static void insertRows(List<MovieDetailsCSV> movieDataList) {
        String sql = "INSERT INTO raw_metadata (" +
                "adult, " +
                "backdropPath, " +
                "collection, " +
                "budget, " +
                "genres, " +
                "homepage, " +
                "id, " +
                "imdbId, " +
                "originCountry, " +
                "originalLanguage, " +
                "originalTitle, " +
                "overview, " +
                "popularity, " +
                "posterPath, " +
                "productionCompanies, " +
                "productionCountries, " +
                "releaseDate, " +
                "revenue, " +
                "runtime, " +
                "spokenLanguages, " +
                "status, " +
                "tagline, " +
                "title, " +
                "video, " +
                "voteAverage, " +
                "vote_count " +
                ") " +
                "VALUES (?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Loop over the list of movie data and add each to the batch
            for (MovieDetailsCSV movieData : movieDataList) {
                pstmt.setString(1, movieData.getAdult());
                pstmt.setString(2, movieData.getBackdropPath());
                pstmt.setString(3, movieData.getCollection());
                pstmt.setString(4, movieData.getBudget());
                pstmt.setString(5, movieData.getGenres());
                pstmt.setString(6, movieData.getHomepage());
                pstmt.setString(7, movieData.getId());
                pstmt.setString(8, movieData.getImdbId());
                pstmt.setString(9, movieData.getOriginCountry());
                pstmt.setString(10, movieData.getOriginalLanguage());
                pstmt.setString(11, movieData.getOriginalTitle());
                pstmt.setString(12, movieData.getOverview());
                pstmt.setString(13, movieData.getPopularity());
                pstmt.setString(14, movieData.getPosterPath());
                pstmt.setString(15, movieData.getProductionCompanies());
                pstmt.setString(16, movieData.getProductionCountries());
                pstmt.setString(17, movieData.getReleaseDate());
                pstmt.setString(18, movieData.getRevenue());
                pstmt.setString(19, movieData.getRuntime());
                pstmt.setString(20, movieData.getSpokenLanguages());
                pstmt.setString(21, movieData.getStatus());
                pstmt.setString(22, movieData.getTagline());
                pstmt.setString(23, movieData.getTitle());
                pstmt.setString(24, movieData.getVideo());
                pstmt.setString(25, movieData.getVoteAverage());
                pstmt.setString(26, movieData.getVote_count());

                // Add the row to the batch
                pstmt.addBatch();
            }

            // Execute the batch of inserts
            int[] rowsAffected = pstmt.executeBatch();
            System.out.println("Batch insert completed. Rows affected: " + rowsAffected.length);

        } catch (SQLException e) {
            System.out.println("Error during batch insert: " + e.getMessage());
        }
    }

    public static List<String> getIds(){
        List<String> idList = new ArrayList<>();

        String query = "SELECT id FROM raw_metadata";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                idList.add(id);
            }

            System.out.println("IDs from raw_metadata: " + idList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idList;
    }
}

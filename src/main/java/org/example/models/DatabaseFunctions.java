package org.example.models;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class DatabaseFunctions {
    private static HikariDataSource dataSource;

    private static final Logger logger = LogManager.getLogger(DatabaseFunctions.class);

    public static boolean initializeDB() {
        try {
            HikariConfig config = new HikariConfig("C:\\Users\\gdumi\\IdeaProjects\\Export_movies\\src\\main\\resources\\db.properties");
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Database initialization failed: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public static void insertRowsDetails(List<MovieDetailsCSV> movieDataList) {
        String sql = "INSERT INTO raw_details_metadata (" +
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

        try (Connection conn = dataSource.getConnection();
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

                pstmt.addBatch();
            }

            int[] rowsAffected = pstmt.executeBatch();
            logger.info("Batch insert completed to raw_details_metadata. Rows affected: {}", rowsAffected.length);

        } catch (SQLException e) {
            logger.error("Error during batch insert: {}", e.getMessage());
        }
    }



    public static void insertRowsCredits(List<MovieCredits> movieDataList) {
        String sql = "INSERT INTO raw_credits_metadata (" +
                "id, " +
                "_cast, " +
                "crew " +
                ") " +
                "VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (MovieCredits movieData : movieDataList) {
                pstmt.setString(1, movieData.getId());
                pstmt.setString(2, movieData.getCast());
                pstmt.setString(3, movieData.getCrew());

                pstmt.addBatch();
            }

            int[] rowsAffected = pstmt.executeBatch();
            logger.info("Batch insert completed to raw_credits_metadata. Rows affected: {}", rowsAffected.length);

        } catch (SQLException e) {
            logger.error("Error during batch insert: {}", e.getMessage());
        }
    }

    public static List<String> getIds(){
        List<String> idList = new ArrayList<>();

        String query = "SELECT id FROM raw_details_metadata";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                idList.add(id);
            }

        } catch (SQLException e) {
            logger.error("Could not get ids from database, exception: {}", e.getMessage());
        }
        return idList.isEmpty() ? Collections.emptyList() : idList;
    }

    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}

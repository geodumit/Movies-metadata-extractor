package org.example.models;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.strings.DatabaseData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseFunctions {
    private static HikariDataSource dataSource;

    private static final String detailsQuery = buildSql(DatabaseData.getDetailsColumns(), DatabaseData.getDetailsTable());
    private static final String creditsQuery = buildSql(DatabaseData.getCreditsColumns(), DatabaseData.getCreditsTable());

    private static final Logger logger = LogManager.getLogger(DatabaseFunctions.class);

    /**
     * Initializes database connection using hikari with configuration parameters from db.properties
     * @param confPath: db.properties file
     * @return: boolean if the initialization was successful
     */
    public static boolean initializeDB(String confPath) {
        try {
            HikariConfig config = new HikariConfig(confPath);
            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Database initialization failed: {}", e.getMessage());
            return false;
        }
        return true;
    }

    private static String buildSql(String[] tableColumns, String tableName) {
        String joinedColumns = String.join(", ", tableColumns);
        String placeholders = String.join(", ", Collections.nCopies(tableColumns.length, "?"));
        return "INSERT INTO " + tableName + " (" + joinedColumns + ") VALUES (" + placeholders + ")";
    }

    public static void insertRowsDetails(List<MovieDetailsCSV> movieDataList) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(detailsQuery)) {

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
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(creditsQuery)) {
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

    /**
     * Get ids of the movies that are in the database
     * @return: List<String> a list of strings containing the ids of the movies
     */
    public static List<String> getDetailsIds(){
        List<String> idList = new ArrayList<>();

        String query = "SELECT movie_id FROM updated_data";

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String id = resultSet.getString("movie_id");
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

    public static boolean runStaticQuery(String query){
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            int rowsAffected = statement.executeUpdate(query);

            logger.info("{} rows affected", rowsAffected);
        } catch (Exception e) {
            logger.error("Exception occured: {}", e.getMessage());
            return false;
        }
        return true;
    }
}

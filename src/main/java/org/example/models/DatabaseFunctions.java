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
import java.util.Optional;

public class DatabaseFunctions implements AutoCloseable {
    private final HikariDataSource dataSource;

    private final String detailsQuery;
    private final String creditsQuery;

    private static final Logger logger = LogManager.getLogger(DatabaseFunctions.class);

    public DatabaseFunctions(String configPath) {
        this.detailsQuery = buildSql(DatabaseData.getDetailsColumns(), DatabaseData.getDetailsTable());
        this.creditsQuery = buildSql(DatabaseData.getCreditsColumns(), DatabaseData.getCreditsTable());
        this.dataSource = initializeDataSource(configPath);
    }

    private HikariDataSource initializeDataSource(String configPath) {
        try {
            HikariConfig config = new HikariConfig(configPath);
            return new HikariDataSource(config);
        } catch (Exception e) {
            logger.error("Failed to initialize database: {}", e.getMessage(), e);
            throw new DatabaseInitializationException("Could not initialize database", e);
        }
    }

    public void insertMovieDetails(List<MovieDetails> movies) {
        if (movies == null || movies.isEmpty()) {
            logger.warn("No movie details to insert");
            return;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(detailsQuery)) {

            for (MovieDetails movie : movies) {
                setMovieDetailsParameters(pstmt, movie);
                pstmt.addBatch();
            }

            executeBatch(pstmt, "details");
        } catch (SQLException e) {
            logger.error("Failed to insert movie details: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Error inserting movie details", e);
        }
    }

    private void setMovieDetailsParameters(PreparedStatement pstmt, MovieDetails movie) throws SQLException {
        pstmt.setString(1, movie.getAdult());
        pstmt.setString(2, movie.getBackdropPath());
        pstmt.setString(3, movie.getCollection());
        pstmt.setString(4, movie.getBudget());
        pstmt.setString(5, movie.getGenres());
        pstmt.setString(6, movie.getHomepage());
        pstmt.setString(7, movie.getId());
        pstmt.setString(8, movie.getImdbId());
        pstmt.setString(9, movie.getOriginCountry());
        pstmt.setString(10, movie.getOriginalLanguage());
        pstmt.setString(11, movie.getOriginalTitle());
        pstmt.setString(12, movie.getOverview());
        pstmt.setString(13, movie.getPopularity());
        pstmt.setString(14, movie.getPosterPath());
        pstmt.setString(15, movie.getProductionCompanies());
        pstmt.setString(16, movie.getProductionCountries());
        pstmt.setString(17, movie.getReleaseDate());
        pstmt.setString(18, movie.getRevenue());
        pstmt.setString(19, movie.getRuntime());
        pstmt.setString(20, movie.getSpokenLanguages());
        pstmt.setString(21, movie.getStatus());
        pstmt.setString(22, movie.getTagline());
        pstmt.setString(23, movie.getTitle());
        pstmt.setString(24, movie.getVideo());
        pstmt.setString(25, movie.getVoteAverage());
        pstmt.setString(26, movie.getVote_count());
        pstmt.setString(27, movie.getImdbRating());
        pstmt.setString(28, movie.getImdbRatingCount());
    }

    private static String buildSql(String[] columns, String tableName) {
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                tableName,
                String.join(", ", columns),
                String.join(", ", Collections.nCopies(columns.length, "?"))
        );
    }

    public void insertMovieCredits(List<MovieCredits> credits) {
        if (credits == null || credits.isEmpty()) {
            logger.warn("No movie credits to insert");
            return;
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(creditsQuery)) {
            for (MovieCredits credit : credits) {
                pstmt.setString(1, credit.getId());
                pstmt.setString(2, credit.getCast());
                pstmt.setString(3, credit.getCrew());
                pstmt.addBatch();

            }
            executeBatch(pstmt, "credits");
        } catch (SQLException e) {
            logger.error("Failed to insert movie credits: {}", e.getMessage(), e);
            throw new DatabaseOperationException("Error inserting movie credits", e);
        }
    }

    private void executeBatch(PreparedStatement pstmt, String operation) throws SQLException {
        int[] results = pstmt.executeBatch();
        int totalUpdates = 0;
        for (int result : results) {
            if (result >= 0) totalUpdates += result;
        }
        logger.info("Batch {} insert completed. Rows affected: {}", operation, totalUpdates);
    }

    /**
     * Get ids of the movies that are in the database
     * @return: List<String> a list of strings containing the ids of the movies
     */
    public List<String> getDetailsIds() {
        String query = "SELECT movie_id FROM updated_data";
        List<String> ids = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                ids.add(rs.getString("movie_id"));
            }
            return ids;

        } catch (SQLException e) {
            logger.error("Failed to retrieve movie IDs: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Override
    public void close() {
        Optional.ofNullable(dataSource).ifPresent(HikariDataSource::close);
    }

    public boolean runStaticQuery(String query){
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

    public static class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DatabaseOperationException extends RuntimeException {
        public DatabaseOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}

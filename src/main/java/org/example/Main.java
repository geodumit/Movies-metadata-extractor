package org.example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.configuration.ConfigCreator;
import org.example.models.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        ConfigCreator config = new ConfigCreator(args);

        String apiKey = config.getApiKey();
        int batchLimit = config.getBatchLimit();
        double moviesPopularity = config.getMoviesPopularity();

        String dbBeforeQueriesPath = config.getBeforeQueriesPath();
        String dbAfterQueriesPath = config.getAfterQueriesPath();
        String dbUpdateDataQueryPath = config.getUpdatedDataQueryPath();
        String dbConfPath = config.getDbConfPath();


        String dbUpdatedDataQuery = HelperFunctions.readFile(new File(dbUpdateDataQueryPath));

        // Get queries that must be run before any other query
        Map<String, FileQuery> beforeQueries = HelperFunctions.getQueries(dbBeforeQueriesPath);

        // Get queries that must be run after the before queries (due to foreign keys)
        Map<String, FileQuery> afterQueries = HelperFunctions.getQueries(dbAfterQueriesPath);

        // Created the directories needed for the downloaded files and the CSVs
        Path downloadedFilesPath = Paths.get("DownloadedFiles");
        Path csvDirectory = Paths.get("MoviesCSV");
        boolean directoriesCreated = HelperFunctions.createDirectories(downloadedFilesPath, csvDirectory);
        if (!directoriesCreated) {
            System.exit(4);
        }

        MoviesCSV moviesCSV = new MoviesCSV();
        boolean csvFileInitialized = moviesCSV.initializeFile(downloadedFilesPath);
        if (!csvFileInitialized) {
            logger.error("CSV File to download could not be initialized");
            System.exit(5);
        }

        boolean csvFileDownloaded = moviesCSV.downloadFile();
        if (!csvFileDownloaded) {
            System.exit(5);
        }

        Path outputFile = moviesCSV.decompress();
        if (outputFile == null) {
            System.exit(5);
        }

        List <Movie> movies;

        movies = HelperFunctions.readMoviesFile(outputFile.toString());

        logger.info("Total movies: {}", movies.size());

        List<Movie> popularMovies = movies.stream()
                .filter(movie -> movie.getPopularity() > moviesPopularity)
                .toList();

        logger.info("Total movies with a popularity higher than {}: {}", moviesPopularity, popularMovies.size());

        List<String> listOfMovieDetails = new ArrayList<>(List.of());
        List<String> listofMovieCredits = new ArrayList<>(List.of());

        boolean databaseInitialized = DatabaseFunctions.initializeDB(dbConfPath);
        if (!databaseInitialized) {
            System.exit(11);
        }

        // get ids of movies in the database
        List<String> idsInDB = DatabaseFunctions.getDetailsIds();

        // filter only the ids that are not in the database
        List<Movie> popularMoviesNotInDB = popularMovies.stream()
                .filter(movie -> !idsInDB.contains(String.valueOf(movie.getId())))
                .toList();

        int totalProcessedMovies = 0;
        int errors = 0;
        int error_limit = 5;
        TimeCalculator timeCalculator = new TimeCalculator();
        TimeCalculator elapsedTime = new TimeCalculator();

        boolean newRun = true;

        int totalMoviesToProcess = popularMoviesNotInDB.size();

        logger.info("Popular movies not in Database: {}", totalMoviesToProcess);

        elapsedTime.startTimer();
        for (int i = 0; i < popularMoviesNotInDB.size(); i++) {

            if (newRun) {
                timeCalculator.startTimer();
                newRun = false;
            }
            int currentId = popularMoviesNotInDB.get(i).getId();

            TMDBAPI tmdbapi = new TMDBAPI(apiKey);
            String movieBodyDetails = tmdbapi.getDetails(currentId);
            if (movieBodyDetails == null) {
                errors += 1;
                logger.warn("Could not get details for {}", currentId);
                continue;
            }

            String movieBodyCredits = tmdbapi.getCredits(currentId);
            if (movieBodyCredits == null) {
                errors += 1;
                logger.warn("Could not get credits for {}", currentId);
                continue;
            }

            if (errors > error_limit) {
                logger.error("Could not get more than {} consecutive details for movies", error_limit);
                System.exit(10);
            }

            if (errors >= error_limit) {
                logger.error("More than {} errors, exiting...", error_limit);
                System.exit(10);
            }

            listOfMovieDetails.add(movieBodyDetails);
            listofMovieCredits.add(movieBodyCredits);
            totalProcessedMovies++;

            if (i % batchLimit == 0 && i != 0) {
                List<MovieDetailsCSV> movieDataList = new ArrayList<>(List.of());
                for (String movie: listOfMovieDetails) {
                    MovieDetailsCSV movieDetailsCSV = new MovieDetailsCSV();
                    movieDetailsCSV.initializeMovie(movie);

                    movieDataList.add(movieDetailsCSV);
                }

                List<MovieCredits> movieCreditsList = new ArrayList<>(List.of());
                for (String movie: listofMovieCredits) {
                    MovieCredits movieCredits = new MovieCredits();
                    movieCredits.initializeMovie(movie);

                    movieCreditsList.add(movieCredits);
                }

                DatabaseFunctions.insertRowsDetails(movieDataList);
                DatabaseFunctions.insertRowsCredits(movieCreditsList);

                logger.info("Running update for updated_data table");
                DatabaseFunctions.runStaticQuery(dbUpdatedDataQuery);

                boolean beforeQueriesRun = HelperFunctions.runQueries(beforeQueries);
                boolean afterQueriesRun = HelperFunctions.runQueries(afterQueries);
                if (!beforeQueriesRun){
                    logger.error("There was an error with running before Queries");
                }
                if (!afterQueriesRun){
                    logger.error("There was an error with running after Queries");
                }

                listOfMovieDetails = new ArrayList<>(List.of());
                listofMovieCredits = new ArrayList<>(List.of());

                newRun = true;
                List<Long> batchTime = timeCalculator.endTimer();
                List<Long> elapsedTimeList = elapsedTime.endTimer();
                List<Long> estimatedTimeList = elapsedTime.calculateEstimatedTime(totalProcessedMovies, totalMoviesToProcess);
                logger.info("Total elapsed Time: {}:{}:{}", elapsedTimeList.get(0), elapsedTimeList.get(1), elapsedTimeList.get(2));
                logger.info("Estimated time to finish: {}:{}:{}", estimatedTimeList.get(0), estimatedTimeList.get(1), estimatedTimeList.get(2));
                logger.info("Batch time: {}:{}:{}", batchTime.get(0), batchTime.get(1), batchTime.get(2));
                logger.info("Total movies processed so far: {}/{}", totalProcessedMovies, totalMoviesToProcess);
            }
        }
    }
}
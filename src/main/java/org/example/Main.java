package org.example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.configuration.ConfigCreator;
import org.example.models.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        int totalProcessedMovies = 0;
        int errors = 0;
        int error_limit = 5;

        // Read configuration parameters from command line arguments
        ConfigCreator config = new ConfigCreator(args);

        String apiKey = config.getApiKey();
        int batchLimit = config.getBatchLimit();
        double moviesPopularity = config.getMoviesPopularity();

        String dbBeforeQueriesPath = config.getBeforeQueriesPath();
        String dbAfterQueriesPath = config.getAfterQueriesPath();
        String dbUpdateDataQueryPath = config.getUpdatedDataQueryPath();
        String dbConfPath = config.getDbConfPath();

        // Read query that insert data to raw tables
        String dbUpdatedDataQuery = HelperFunctions.readFile(new File(dbUpdateDataQueryPath));

        // Get queries that must be first, they don't have any foreign keys
        Map<String, FileQuery> beforeQueries = HelperFunctions.getQueries(dbBeforeQueriesPath);

        // Get queries that must be run after the first queries, they have foreign keys
        Map<String, FileQuery> afterQueries = HelperFunctions.getQueries(dbAfterQueriesPath);

        // Create the directories needed for the downloaded files and the CSVs
        Path downloadedFilesPath = Paths.get("DownloadedFiles");
        boolean directoriesCreated = HelperFunctions.createDirectories(downloadedFilesPath);
        if (!directoriesCreated) {
            System.exit(4);
        }

        // Initialize the csv of the total movie ids of TMDB
        MoviesCSV moviesCSV = new MoviesCSV();
        boolean csvFileInitialized = moviesCSV.initializeFile(downloadedFilesPath);
        if (!csvFileInitialized) {
            logger.error("CSV File to download could not be initialized");
            System.exit(5);
        }

        // Download the compressed file
        boolean csvFileDownloaded = moviesCSV.downloadFile();
        if (!csvFileDownloaded) {
            System.exit(5);
        }

        // Decompress the file
        Path outputFile = moviesCSV.decompress();
        if (outputFile == null) {
            System.exit(5);
        }

        // Create list of Movie objects that contains the data from the file downloaded
        List <Movie> movies;
        movies = HelperFunctions.readMoviesFile(outputFile.toString());

        logger.info("Total movies: {}", movies.size());

        // Filter the movies, get only the movies with a popularity higher that the one set in the configuration file
        List<Movie> popularMovies = movies.stream()
                .filter(movie -> movie.getPopularity() > moviesPopularity)
                .toList();

        logger.info("Total movies with a popularity higher than {}: {}", moviesPopularity, popularMovies.size());

        // Create two lists, containing movie details metadata and movie credits metadata
        List<String> listOfMovieDetails = new ArrayList<>(List.of());
        List<String> listofMovieCredits = new ArrayList<>(List.of());

        // Initialize the database using hikari
        boolean databaseInitialized = DatabaseFunctions.initializeDB(dbConfPath);
        if (!databaseInitialized) {
            System.exit(11);
        }

        // Get ids of movies in the database
        List<String> idsInDB = DatabaseFunctions.getDetailsIds();

        // filter only the ids that are not in the database
        List<Movie> popularMoviesNotInDB = popularMovies.stream()
                .filter(movie -> !idsInDB.contains(String.valueOf(movie.getId())))
                .toList();

        // Time calculator objects to count the time passed for each batch, estimated time etc
        TimeCalculator timeCalculator = new TimeCalculator();
        TimeCalculator elapsedTime = new TimeCalculator();

        // Used to know when a new batch is starting
        boolean newRun = true;

        int totalMoviesToProcess = popularMoviesNotInDB.size();
        logger.info("Popular movies not in Database: {}", totalMoviesToProcess);

        logger.info("Extracting metadata...");
        elapsedTime.startTimer();

        TMDBAPI tmdbapi = new TMDBAPI(apiKey);
        ImdbApi imdbapi = new ImdbApi();

        for (int i = 0; i < popularMoviesNotInDB.size(); i++) {

            if (newRun) {
                timeCalculator.startTimer();
                newRun = false;
            }
            int currentId = popularMoviesNotInDB.get(i).getId();

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
                List<MovieCredits> movieCreditsList = new ArrayList<>(List.of());
                for (String movie: listofMovieCredits) {
                    MovieCredits movieCredits = new MovieCredits();
                    movieCredits.initializeMovie(movie);

                    movieCreditsList.add(movieCredits);
                }
                List<MovieDetails> movieDataList = new ArrayList<>();
                ExecutorService executor = Executors.newFixedThreadPool(10);
                List<MovieProcessor> processors = new ArrayList<>();

                // Create and submit tasks
                for (String movie : listOfMovieDetails) {
                    MovieProcessor processor = new MovieProcessor(movie, imdbapi);
                    processors.add(processor);
                    executor.submit(processor);
                }

                // Shutdown executor and wait for all tasks to complete
                executor.shutdown();
                try {
                    executor.awaitTermination(30, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("Thread interrupted while waiting for movie processing");
                }

                // Collect results
                for (MovieProcessor processor : processors) {
                    movieDataList.add(processor.getMovieDetails());
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
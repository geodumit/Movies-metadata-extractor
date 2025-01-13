package org.example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.configuration.ConfigLoader;
import org.example.models.*;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args) {

        ConfigLoader config = new ConfigLoader("C:\\Users\\gdumi\\IdeaProjects\\Export_movies\\src\\main\\resources\\config.properties");
        if (!config.configIsOk()) {
            System.exit(3);
        }

        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        // Check Arguments for apiKey or configuration file for it
        String apiKeyArg = argumentsParser.getApiKey();
        String apiKey = null;
        if (apiKeyArg == null){
            if (config.apiKeyIsOk()){
                apiKey = config.getApiKey();
            } else {
                logger.error("There is no API Key provided in configuration file");
                System.exit(3);
            }
        } else {
            apiKey = apiKeyArg;
        }

        // Check Arguments for batch Limit or configuration file for it
        int batchLimitArg = argumentsParser.getBatchLimit();


        System.exit(0);
        int batchLimit = config.getBatchLimit();
        double moviesPopularity = config.getMoviesPopularity();
        String dbBeforeQueriesPath = config.getBeforeQueriesPath();
        String dbAfterQueriesPath = config.getAfterQueriesPath();
        String dbUpdateDataQueryPath = config.getUpdatedDataQueryPath();

        String dbUpdatedDataQuery = HelperFunctions.readFile(new File(dbUpdateDataQueryPath));

        // Get queries that must be run before any other query
        Map<String, FileQuery> beforeQueries = HelperFunctions.getQueries(dbBeforeQueriesPath);

        // Get queries that must be run after the before queries (due to foreign keys)
        Map<String, FileQuery> afterQueries = HelperFunctions.getQueries(dbAfterQueriesPath);


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

        boolean databaseInitialized = DatabaseFunctions.initializeDB();
        if (!databaseInitialized) {
            System.exit(11);
        }

        // get ids of movies in the database
        List<String> idsInDB = DatabaseFunctions.getDetailsIds();

        // filter only the ids that are not in the database
        List<Movie> popularMoviesNotInDB = popularMovies.stream()
                .filter(movie -> !idsInDB.contains(String.valueOf(movie.getId())))
                .toList();

        int errors = 0;
        int error_limit = 5;
        long startTime = 0;
        long endTime = 0;
        long totalTime = 0;

        boolean newRun = true;

        logger.info("Popular movies not in Database: {}", popularMoviesNotInDB.size());

        for (int i = 0; i < popularMoviesNotInDB.size(); i++) {
            if (newRun) {
                startTime = System.nanoTime();
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
                logger.warn("Could not get details for {}", currentId);
                continue;
            }


            if (errors > error_limit) {
                logger.error("Could not get more than {} consecutive details for movies", error_limit);
                System.exit(10);
            }

            listOfMovieDetails.add(movieBodyDetails);
            listofMovieCredits.add(movieBodyCredits);

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
                endTime = System.nanoTime();
                totalTime = endTime - startTime;

                Date myTime = new Date(totalTime / 1000);
                System.out.println(myTime);
            }

            logger.info("iteration: {}", i);
        }
    }
}
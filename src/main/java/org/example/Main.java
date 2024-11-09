package org.example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);

    public static void main(String[] args){

        ConfigLoader config = new ConfigLoader("C:\\Users\\gdumi\\IdeaProjects\\Export_movies\\src\\main\\resources\\config.properties");
        if (!config.configIsOk()){
            System.exit(3);
        }

        String apiKey = config.getapiKey();
        int csvLimit = config.getCsvLimit();
        double moviesPopularity = config.getMoviesPopularity();

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

        // get ids of movies in the database
        List<String> idsInDB = DatabaseFunctions.getIds();

        // filter only the ids that are not in the database
        List<Movie> popularMoviesNotInDB = popularMovies.stream()
                .filter(movie -> !idsInDB.contains(String.valueOf(movie.getId())))
                .toList();

        int errors = 0;
        int error_limit = 5;


        for (int i = 0; i < popularMoviesNotInDB.size(); i++) {
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

            if (i % csvLimit == 0 && i != 0) {
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

                listOfMovieDetails = new ArrayList<>(List.of());
                listofMovieCredits = new ArrayList<>(List.of());
            }

            logger.info("iteration: {}", i);
        }
    }
}
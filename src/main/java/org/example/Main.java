package org.example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.example.models.HelperFunctions.getCurrentDate;

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

        List<String> listOfMovies = new java.util.ArrayList<>(List.of());

        int errors = 0;
        int error_limit = 5;

        for (int i = 0; i < popularMovies.size(); i++) {
            int currentId = popularMovies.get(i).getId();

            TMDBAPI tmdbapi = new TMDBAPI(apiKey);
            String movie_body = tmdbapi.getDetails(currentId);
            if (movie_body == null) {
                errors += 1;
                logger.warn("Could not get details for {}", currentId);
                continue;
            }

            if (errors > error_limit) {
                logger.error("Could not get more than {} consecutive details for movies", error_limit);
            }

            System.out.println(movie_body);
            listOfMovies.add(movie_body);

            if (i % csvLimit == 0 && i != 0) {
                String csvDate = getCurrentDate("yyyyMMddHHmmss");
                String csvFile = "movies_csv_" + csvDate + ".csv";
                Path csvFilePath = csvDirectory.resolve(csvFile);

                StringBuilder moviesData = new StringBuilder(MovieDetailsCSV.getColumns());
                moviesData.append("\n");
                for (String movie: listOfMovies) {
                    MovieDetailsCSV movieDetailsCSV = new MovieDetailsCSV();
                    movieDetailsCSV.initializeMovie(movie);
                    moviesData.append(movieDetailsCSV.getCSVRow()).append("\n");
                }
                try {
                    FileWriter writer = new FileWriter(csvFilePath.toString(), true);
                    writer.write(String.valueOf(moviesData));
                    writer.close();
                    System.out.println("File written successfully!");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.exit(0);
                listOfMovies = new java.util.ArrayList<>(List.of());
            }

            logger.info("iteration: {}", i);
            errors = 0;
        }
    }
}
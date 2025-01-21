package org.example.models;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;
import java.util.zip.GZIPInputStream;


public class HelperFunctions {
    private static final Logger logger = LogManager.getLogger(HelperFunctions.class);

    /**
     * Creates directories DownloadedFiles and MoviesCSV if they are not present
     * @param paths paths of directories to create
     * @return boolean if created
     */
    public static boolean createDirectories(Path... paths){
        for (Path pathToCreate: paths) {
            boolean pathCreated = createDirectory(pathToCreate);
            if (!pathCreated) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates directory in the location of the param path
     * @param path path of directory to create
     * @return boolean if created or not
     */
    public static boolean createDirectory(Path path){
        logger.info("Creating Directory...");

        try {
            Files.createDirectory(path);
            logger.info("Directory: {} created successfully!", path);
        } catch (FileAlreadyExistsException e) {
            logger.info("Directory {} already exists", path);
        } catch (IOException e) {
            logger.error("Failed to create directory: {}, exception {} ", path, e);
            return false;
        }
        return true;
    }


    public static String getCurrentDate(String pattern){
        ZonedDateTime currentDate = ZonedDateTime.now(ZoneOffset.UTC);
        logger.info("Got current UTC date: {}", currentDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        String formattedDate = currentDate.format(formatter);

        logger.info("Formatted UTC date: {}", formattedDate);
        return formattedDate;
    }


    public static void downloadFile(String fileURL, String saveFilePath) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
        int responseCode = httpConnection.getResponseCode();

        // Check if the response code is HTTP OK (200)
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // Opens input stream from the HTTP connection
            InputStream inputStream = new BufferedInputStream(httpConnection.getInputStream());

            // Opens an output stream to save the downloaded file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            byte[] buffer = new byte[4096];
            int bytesRead;

            // Reads the data from the input stream and writes to the output file
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Close streams
            outputStream.close();
            inputStream.close();
            logger.info("File downloaded to: {}", saveFilePath);
        } else {
            logger.error("No file to download. Server replied with HTTP code: {}", responseCode);
        }
        httpConnection.disconnect();
    }

    // Method to decompress a .gz file
    public static void decompressGzipFile(String gzipFile, String newFile) throws IOException {
        byte[] buffer = new byte[1024];

        // Create GZIPInputStream to read the compressed file
        try (GZIPInputStream gis = new GZIPInputStream(new FileInputStream(gzipFile));
             FileOutputStream fos = new FileOutputStream(newFile)) {

            int bytesRead;
            while ((bytesRead = gis.read(buffer)) > 0) {
                // Write the decompressed data to the output file
                fos.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * converts JSON data to java object data and added to a movies list
     * @param filePath: The path of the file containing the json data
     * @return: List<Movie> of the movies contained in the json file
     */
    public static List<Movie> readMoviesFile(String filePath) {
        List<Movie> movies = new ArrayList<>();
        Gson gson = new Gson();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Movie movie = gson.fromJson(line, Movie.class);
                movies.add(movie);
            }
        } catch (IOException e) {
            logger.error("Couldn't read json file: {}", filePath);
        }

        return movies;
    }

    /**
     * Reads the data of a file and returns the content of it
     * @param file: the file that will be read
     * @return: the contents of the file read
     */
    public static String readFile(File file){
        String content;
        try {
            Path filePath = Path.of(file.toURI());
            content = Files.readString(filePath);
        } catch (IOException e) {
            logger.error("Could not read {}, exception: {}", file.getName(), e.getMessage());
            return null;
        }
        return content;
    }

    /**
     * Reads the files from a path and reads the contents of the insert_query.sql and update_query.sql
     * @param dbQueriesPath: The path that contains the files with the queries
     * @return: Returns a map of the queries found in the path of dbQueriesPath
     */
    public static Map<String, FileQuery> getQueries(String dbQueriesPath){
        File[] queriesDirectory = FolderLister.getFiles(dbQueriesPath);
        Map<String, File[]> queriesFiles = new HashMap<>();

        for (File file: queriesDirectory) {
            queriesFiles.put(file.getName(), FolderLister.getFiles(file));
        }

        Map<String, FileQuery> queries = new HashMap<>();

        for (Map.Entry<String, File[]> entry : queriesFiles.entrySet()) {
            String keyValue = entry.getKey();
            String insertQuery = null;
            String updateQuery = null;
            for (File query: entry.getValue()) {
                if (query.getName().equals("insert_query.sql")){
                    insertQuery = HelperFunctions.readFile(query);
                }
                if (query.getName().equals("update_query.sql")) {
                    updateQuery = HelperFunctions.readFile(query);
                }
            }
            FileQuery fileQuery = new FileQuery(keyValue, insertQuery, updateQuery);
            queries.put(keyValue, fileQuery);
        }
        return queries;
    }

    public static void runQueries(Map<String, FileQuery> queries) {
        boolean insertQueryRun = true;
        boolean updateQueryRun = true;
        for (Map.Entry<String, FileQuery> entry : queries.entrySet()) {
            String keyValue = entry.getKey();
            String insertQuery = entry.getValue().getInsertQuery();
            String updateQuery = entry.getValue().getUpdateQuery();
            logger.info("Running queries for {} table", keyValue);
            if (insertQuery != null) {
                logger.info("Data insert for table: {}", keyValue);
                insertQueryRun = DatabaseFunctions.runStaticQuery(insertQuery);
            } else {
                logger.warn("No insert query for {}", keyValue);
            }

            if (updateQuery != null) {
                logger.info("Data update for table: {}", keyValue);
                updateQueryRun = DatabaseFunctions.runStaticQuery(updateQuery);
            } else {
                logger.warn("No update query for {}", keyValue);
            }
        }
        if (!insertQueryRun || !updateQueryRun) {
            throw new IllegalArgumentException("Queries could not be run");
        }
    }

    public static ImdbRating extractImdbRating(String jsonRating) {
        if (jsonRating == null || jsonRating.isEmpty()) {
            throw new IllegalArgumentException("JSON rating string cannot be null or empty");
        }

        try {
            JSONObject movie = new JSONObject(jsonRating);
            String rating = movie.getString("rating");
            String ratingCount = movie.getString("rating_count");

            return new ImdbRating(rating, ratingCount);
        } catch (JSONException e) {
            throw new IllegalArgumentException("Invalid JSON format or missing required fields", e);
        }
    }
}

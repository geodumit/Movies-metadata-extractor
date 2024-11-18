package org.example.models;

import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
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
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    public static String readFile(File file){
        String content;
        try {
            Path filePath = Path.of(file.toURI());
            content = Files.readString(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content;
    }
}

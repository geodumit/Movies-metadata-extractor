package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static org.example.models.HelperFunctions.decompressGzipFile;
import static org.example.models.HelperFunctions.getCurrentDate;

public class MoviesCSV {
    String currentDate;
    String fileToDownload;
    Path filePath;
    String url;
    String jsonFile;
    Path outputFile;

    private static final Logger logger = LogManager.getLogger(MoviesCSV.class);

    /**
     * prepares file names for: the file needed to be downloaded, the url of the file to download, file path of the
     * downloaded file, json file
     * @param downloadedFilesPath: The path of where the files will be created
     * @return: return true if the files where initialized successfully
     */
    public boolean initializeFile(Path downloadedFilesPath) {
        currentDate = getCurrentDate("MM_dd_yyyy");

        fileToDownload = "movie_ids_" + currentDate + ".json.gz";
        logger.info("File to download: {}", fileToDownload);

        url = "https://files.tmdb.org/p/exports/" + fileToDownload;
        logger.info("URL for file to download: {}", url);

        filePath = downloadedFilesPath.resolve(fileToDownload);
        logger.info("Path of file: {}", filePath);

        jsonFile = fileToDownload.replace(".gz", "");
        outputFile = downloadedFilesPath.resolve(jsonFile);
        logger.info("JSON file path: {}", outputFile);

        return true;
    }

    /**
     * Downloads the file that contains the ids of the movies in TMDB
     * @return: boolean, if the file was downloaded
     */
    public boolean downloadFile(){
        try {
            HelperFunctions.downloadFile(url, filePath.toString());
        } catch (IOException e) {
            logger.error("Error while downloading file from url: {}", url);
            return false;
        }
        return true;
    }

    /**
     * Decompresses the file that was downloaded
     * @return: the compressed file
     */
    public Path decompress(){
        try {
            decompressGzipFile(filePath.toString(), outputFile.toString());
            logger.info("File: {} decompressed successfully to {}", filePath, outputFile);
        } catch (IOException e) {
            logger.error("Error while decompressing file: {}", filePath);
            return null;
        }
        return outputFile;
    }
}

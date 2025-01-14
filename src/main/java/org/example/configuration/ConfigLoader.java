package org.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigLoader {
    private final Properties properties = new Properties();
    private String apiKey;
    private int batchLimit;
    private double moviesPopularity;

    public int getBatchLimit() {
        return batchLimit;
    }

    public double getMoviesPopularity() {
        return moviesPopularity;
    }

    public String getApiKey() {
        return apiKey;
    }

    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);

    public ConfigLoader(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Configuration file could not be read, exception: {}", e.getMessage());
        }
    }

    public boolean apiKeyIsOk(){
        boolean valueToReturn;
        apiKey = properties.getProperty("api.key");
        if (apiKey == null) {
            logger.error("api.key doesn't have a valid value");
            valueToReturn = false;
        } else {
            valueToReturn = true;
        }
        return valueToReturn;
    }

    public boolean batchLimitIsOk(){
        try {
            String batchLimitString = properties.getProperty("csv.limit");
            if (batchLimitString == null) {
                logger.error("batch.limit doesn't have a valid value");
                return false;
            }
            batchLimit = Integer.parseInt(batchLimitString);
        } catch (NumberFormatException e) {
            logger.error("batch limit doesn't have a valid value in configuration file, exception: {}", e.getMessage());
            return false;
        }
        return true;
    }

    public boolean moviesPopularityIsOk() {
        try {
            String moviesPopularityString = properties.getProperty("movies.popularity");
            if (moviesPopularityString == null) {
                logger.error("movies.popularity doesn't have a valid value");
                return false;
            }
            moviesPopularity = Double.parseDouble(moviesPopularityString);
        } catch (NullPointerException | NumberFormatException e) {
            logger.error("movies.popularity doesn't have a valid value, exception: {}", e.getMessage());
            return false;
        }
        return true;
    }
}

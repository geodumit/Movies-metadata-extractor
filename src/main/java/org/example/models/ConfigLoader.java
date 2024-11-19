package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class ConfigLoader {
    private final Properties properties = new Properties();
    private String apiKey;
    private int csvLimit;
    private double moviesPopularity;
    private String queriesPath;
    private String updatedDataQueryPath;

    public int getCsvLimit() {
        return csvLimit;
    }

    public double getMoviesPopularity() {
        return moviesPopularity;
    }

    public String getapiKey() {
        return apiKey;
    }

    public String getQueriesPath() {
        return queriesPath;
    }

    public String getUpdatedDataQueryPath() {
        return updatedDataQueryPath;
    }

    private static final Logger logger = LogManager.getLogger(ConfigLoader.class);

    public ConfigLoader(String filePath) {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException e) {
            logger.error("Configuration file could not be read, exception: {}", e.getMessage());
        }

    }

    public Boolean configIsOk() {
        apiKey = properties.getProperty("api.key");
        if (apiKey == null) {
            logger.error("api.key doesn't have a valid value");
            return false;
        }

        try {
            String csvLimitString = properties.getProperty("csv.limit");
            if (csvLimitString == null) {
                logger.error("csv.limit doesn't have a valid value");
                return false;
            }
            csvLimit = Integer.parseInt(csvLimitString);
        } catch (NumberFormatException e) {
            logger.error("csv_limit doesn't have a valid value, exception: {}", e.getMessage());
            return false;
        }

        try {
            moviesPopularity = Double.parseDouble(properties.getProperty("movies.popularity"));
        } catch (NullPointerException | NumberFormatException e) {
            logger.error("movies.popularity doesn't have a valid value, exception: {}", e.getMessage());
            return false;
        }

        queriesPath = properties.getProperty(("db.queriesPath"));
        if (queriesPath == null) {
            logger.error("db.queriesPath doesn't have a valid value");
            return false;
        }

        updatedDataQueryPath = properties.getProperty("db.updatedDataQuery");
        if (updatedDataQueryPath == null) {
            logger.error("db.updatedDataQuery doesn't have a valid value");
            return false;
        }

        logger.info("Configuration file is ok");
        return true;
    }
}

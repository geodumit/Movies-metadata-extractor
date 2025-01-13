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
    private String beforeQueriesPath;
    private String updatedDataQueryPath;
    private String afterQueriesPath;

    public int getBatchLimit() {
        return batchLimit;
    }

    public double getMoviesPopularity() {
        return moviesPopularity;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getBeforeQueriesPath() {
        return beforeQueriesPath;
    }

    public String getUpdatedDataQueryPath() {
        return updatedDataQueryPath;
    }

    public String getAfterQueriesPath() {
        return afterQueriesPath;
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

    public Boolean configIsOk() {
        try {
            moviesPopularity = Double.parseDouble(properties.getProperty("movies.popularity"));
        } catch (NullPointerException | NumberFormatException e) {
            logger.error("movies.popularity doesn't have a valid value, exception: {}", e.getMessage());
            return false;
        }

        beforeQueriesPath = properties.getProperty(("db.beforeQueriesPath"));
        if (beforeQueriesPath == null) {
            logger.error("db.beforeQueriesPath doesn't have a valid value");
            return false;
        }

        afterQueriesPath = properties.getProperty(("db.afterQueriesPath"));
        if (afterQueriesPath == null) {
            logger.error("db.afterQueriesPath doesn't have a valid value");
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

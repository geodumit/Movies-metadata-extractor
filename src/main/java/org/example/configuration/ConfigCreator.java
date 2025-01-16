package org.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigCreator {
    private String apiKey;
    private int batchLimit;
    private double moviesPopularity;
    private final String beforeQueriesPath;
    private final String updatedDataQueryPath;
    private final String afterQueriesPath;
    private final String dbConfPath;

    public String getDbConfPath() {
        return dbConfPath;
    }

    public String getApiKey() {
        return apiKey;
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    public double getMoviesPopularity() {
        return moviesPopularity;
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

    private static final Logger logger = LogManager.getLogger(ConfigCreator.class);

    /**
     * Used to create a configuration object that gets data from command line arguments
     * @param args: arguments from command line
     */
    public ConfigCreator(String[] args) {

        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        // Check Arguments for apiKey or configuration file for it
        String apiKeyArg = argumentsParser.getApiKey();
        apiKey = null;
        if (apiKeyArg == null){
            logger.error("There is no API Key provided");
            System.exit(1);
        } else {
            logger.info("Api key provided from command line argument");
            apiKey = apiKeyArg;
        }

        // Check Arguments for batch Limit or configuration file for it
        int batchLimitArg = argumentsParser.getBatchLimit();
        batchLimit = 50;
        if (batchLimitArg == -1) {
            logger.warn("There is no batch limit provided, using default: 50");
        } else {
            batchLimit = batchLimitArg;
            logger.info("Batch Limit provided, value: {}", batchLimit);
        }

        beforeQueriesPath = argumentsParser.getBeforeQueriesPath();
        if (beforeQueriesPath == null) {
            logger.error("There is no before queries path provided");
        } else {
            logger.info("Before queries path provided, path: {}", beforeQueriesPath);
        }

        afterQueriesPath = argumentsParser.getAfterQueriesPath();
        if (afterQueriesPath == null) {
            logger.error("There is no after queries path provided");
            System.exit(1);
        } else {
            logger.info("After queries path provided, path: {}", afterQueriesPath);
        }

        updatedDataQueryPath = argumentsParser.getUpdateQueryPath();
        if (updatedDataQueryPath == null) {
            logger.error("There is no update query path provided");
            System.exit(1);
        } else {
            logger.info("Update query path provided, path: {}", updatedDataQueryPath);
        }

        dbConfPath = argumentsParser.getDbConfPath();
        if (dbConfPath == null) {
            logger.error("There is no DB configuration file provided");
            System.exit(1);
        } else {
            logger.info("DB configuration path provided, path: {}", dbConfPath);
        }

        double moviesPopularityArg = argumentsParser.getMoviesPopularity();
        moviesPopularity = 3.5;
        if (moviesPopularityArg == -1) {
            logger.warn("There is no movies popularity provided, using default: 3.5");
        } else {
            logger.info("Movies popularity provided from command line argument");
            moviesPopularity = moviesPopularityArg;
        }

    }
}

package org.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigCreator {
    private String apiKey;
    private int batchLimit;
    private double moviesPopularity;
    private String beforeQueriesPath;
    private String updatedDataQueryPath;
    private String afterQueriesPath;

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

    public ConfigCreator(String[] args) {
        ConfigLoader config = new ConfigLoader("C:\\Users\\gdumi\\IdeaProjects\\Export_movies\\src\\main\\resources\\config.properties");

        ArgumentsParser argumentsParser = new ArgumentsParser(args);

        // Check Arguments for apiKey or configuration file for it
        String apiKeyArg = argumentsParser.getApiKey();
        apiKey = null;
        if (apiKeyArg == null){
            if (config.apiKeyIsOk()){
                logger.info("Api key provided from configuration file");
                apiKey = config.getApiKey();
            } else {
                logger.error("There is no API Key provided in configuration file");
                System.exit(3);
            }
        } else {
            logger.info("Api key provided from command line argument");
            apiKey = apiKeyArg;
        }

        // Check Arguments for batch Limit or configuration file for it
        int batchLimitArg = argumentsParser.getBatchLimit();
        batchLimit = 50;
        if (batchLimitArg == -1) {
            if (config.batchLimitIsOk()) {
                batchLimit = config.getBatchLimit();
                logger.info("Batch limit provided from configuration file, value: {}", batchLimit);
            } else {
                logger.warn("There is no batch limit provided in configuration file, default is 50");
            }
        } else {
            logger.info("Batch Limit provided from command line argument");
            batchLimit = batchLimitArg;
        }

        double moviesPopularityArg = argumentsParser.getMoviesPopularity();
        moviesPopularity = 3.5;
        if (moviesPopularityArg == -1) {
            if (config.moviesPopularityIsOk()) {
                moviesPopularity = config.getMoviesPopularity();
                logger.info("Movies popularity provided from configuration file, value: {}", moviesPopularity);
            } else {
                logger.warn("There is no movies popularity provided in configuration file, default is 3.5");
            }
        } else {
            logger.info("Movies popularity provided from command line argument");
            moviesPopularity = moviesPopularityArg;
        }

    }
}

package org.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.MoviesCSV;

public class ArgumentsParser {
    String apiKey;
    int batchLimit;
    double moviesPopularity;
    String configurationFile;

    public String getConfigurationFile() {
        return configurationFile;
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

    private static final Logger logger = LogManager.getLogger(ArgumentsParser.class);

    public ArgumentsParser(String[] args){
        String batchLimitString = null;
        String moviesPopularityString = null;
        apiKey = null;
        batchLimit = -1;
        moviesPopularity = -1;
        configurationFile = null;

        for (int i = 0; i < args.length; i++) {
            if ("-conf".equals(args[i])){
                if (i + 1 < args.length) {
                    configurationFile = args[i + 1];
                } else {
                    logger.warn("No value provided for -conf argument");
                    configurationFile = null;
                }
            }

            if ("-apiKey".equals(args[i])) {
                if (i + 1 < args.length) {
                    apiKey = args[i + 1];
                } else {
                    logger.warn("No value provided for -apiKey argument");
                    apiKey = null;
                }
            }

            if ("-bLimit".equals(args[i])) {
                if (i + 1 < args.length) {
                    batchLimitString = args[i + 1];
                } else {
                    logger.warn("No value provided for -bLimit argument");
                    batchLimitString = null;
                }
            }

            if ("-moviePop".equals(args[i])) {
                if (i + 1 < args.length) {
                    moviesPopularityString = args[i + 1];
                } else {
                    logger.warn("No value provided for -moviePop argument");
                    moviesPopularityString = null;
                }
            }
        }

        // Convert the batch limit argument to int
        if (batchLimitString != null) {
            try {
                batchLimit = Integer.parseInt(batchLimitString);
            } catch (NumberFormatException e) {
                logger.warn("The batch limit provided in argument is not valid");
            }
        }

        // Convert the movies popularity argument to double
        if (moviesPopularityString != null) {
            try {
                moviesPopularity = Double.parseDouble(moviesPopularityString);
            } catch (NumberFormatException e) {
                logger.warn("The movies popularity provided in argument is not valid");
            }
        }
    }
}

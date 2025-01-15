package org.example.configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.models.MoviesCSV;

public class ArgumentsParser {
    String apiKey;
    int batchLimit;
    double moviesPopularity;
    String beforeQueriesPath;
    String afterQueriesPath;
    String updateQueryPath;
    String dbConfPath;

    public String getDbConfPath() {
        return dbConfPath;
    }

    public String getBeforeQueriesPath() {
        return beforeQueriesPath;
    }

    public String getAfterQueriesPath() {
        return afterQueriesPath;
    }

    public String getUpdateQueryPath() {
        return updateQueryPath;
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
        beforeQueriesPath = null;
        afterQueriesPath = null;
        updateQueryPath = null;

        for (int i = 0; i < args.length; i++) {

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

            if ("-bqPath".equals(args[i])){
                if (i + 1 < args.length) {
                    beforeQueriesPath = args[i + 1];
                } else {
                    logger.warn("No value provided for -bqPath argument");
                    beforeQueriesPath = null;
                }
            }

            if ("-aqPath".equals(args[i])){
                if (i + 1 < args.length) {
                    afterQueriesPath = args[i + 1];
                } else {
                    logger.warn("No value provided for -aqPath argument");
                    afterQueriesPath = null;
                }
            }

            if ("-uqPath".equals(args[i])){
                if (i + 1 < args.length) {
                    updateQueryPath = args[i + 1];
                } else {
                    logger.warn("No value provided for -uqPath argument");
                    updateQueryPath = null;
                }
            }

            if ("-dbConf".equals(args[i])){
                if (i + 1 < args.length) {
                    dbConfPath = args[i + 1];
                } else {
                    logger.warn("No value provided for -dbConf argument");
                    dbConfPath = null;
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

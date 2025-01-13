package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArgumentsParser {
    String apiKey;
    int batchLimit;

    public String getApiKey() {
        return apiKey;
    }

    public int getBatchLimit() {
        return batchLimit;
    }

    private static final Logger logger = LogManager.getLogger(MoviesCSV.class);

    public ArgumentsParser(String[] args){
        String batchLimitString = null;
        apiKey = null;

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
                    batchLimit = -1;
                }
            }
        }

        if (batchLimitString != null) {
            try {
                batchLimit = Integer.parseInt(batchLimitString);
            } catch (NumberFormatException e) {
                logger.warn("The batch limit provided in argument is not valid");
                batchLimit = -1;
            }
        }
    }
}

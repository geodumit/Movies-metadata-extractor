#!/bin/bash

CONFIGURATION_FILE="./real_params.config"
SCRIPT_DIR=$(dirname "$0")
BEFORE_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/Before"
AFTER_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/After"
UPDATE_QUERY_PATH="$SCRIPT_DIR/docker/Queries/UpdatedData/insert_query.sql"
DB_CONF_PATH="$SCRIPT_DIR/db.properties"

# Check if the configuration file exists
if [ ! -e "$CONFIGURATION_FILE" ]; then
    echo "The configuration file '$CONFIGURATION_FILE' does not exist."
    exit 1
fi

# Check if the path of before queries exists
if [ ! -d "$BEFORE_QUERIES_PATH" ]; then
    echo "The Before queries path '$BEFORE_QUERIES_PATH' does not exist."
    exit 1
fi

# Check if the path of after queries exists
if [ ! -d "$AFTER_QUERIES_PATH" ]; then
    echo "The Before queries path '$AFTER_QUERIES_PATH' does not exist."
    exit 1
fi

# Check if the update query file exists
if [ ! -e "$UPDATE_QUERY_PATH" ]; then
    echo "The file '$UPDATE_QUERY_PATH' does not exist."
    exit 1
fi

#Check if the DB configuration file exists
if [ ! -e "$DB_CONF_PATH" ]; then
    echo "The file "$DB_CONF_PATH" does not exist."
    exit 1
fi

# Extracts the values to corresponding variables
while IFS='=' read -r key value; do
    # Remove leading/trailing whitespace from key and value
    key=$(echo "$key" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    value=$(echo "$value" | sed 's/^[[:space:]]*//;s/[[:space:]]*$//')
    
    if [ -n "$key" ]; then
        case "$key" in
            "api.key") API_KEY="$value" ;;
            "batch.limit") BATCH_LIMIT="$value" ;;
            "movies.popularity") MOVIES_POPULARITY="$value" ;;
            "jar.path") JAR_PATH="$value" ;;
	    "java.path") JAVA_PATH="$value" ;;
        esac
    fi
done < "$CONFIGURATION_FILE"

"$JAVA_PATH" -jar "$JAR_PATH" -apiKey "$API_KEY" -bLimit "$BATCH_LIMIT" -moviePop "$MOVIES_POPULARITY" -bqPath "$BEFORE_QUERIES_PATH" -aqPath "$AFTER_QUERIES_PATH" -uqPath "$UPDATE_QUERY_PATH" -dbConf "$DB_CONF_PATH"

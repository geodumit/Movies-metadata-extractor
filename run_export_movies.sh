#!/bin/bash

CONFIGURATION_FILE="./params.config"
SCRIPT_DIR=$(dirname "$0")
BEFORE_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/Before"
AFTER_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/After"
UPDATE_QUERY_PATH="$SCRIPT_DIR/docker/Queries/UpdatedData/insert_query.sql"
DB_CONF_PATH="$SCRIPT_DIR/db.properties"

#Checks if file exists
check_if_file_exists() {
    local FILE="$1"

    if [ ! -e "$FILE" ]; then
        echo "The file '$FILE' does not exist."
        exit 1
    fi
}

#Checks if directory exists
check_if_directory_exists() {
    local DIRECTORY="$1"

    if [ ! -d "$DIRECTORY" ]; then
	 echo "The directory "$DIRECTORY" does not exist."
	 exit 1
    fi
}

check_if_file_exists "$CONFIGURATION_FILE"
check_if_file_exists "$UPDATE_QUERY_PATH"
check_if_file_exists "$DB_CONF_PATH"
check_if_directory_exists "$BEFORE_QUERIES_PATH"
check_if_directory_exists "$AFTER_QUERIES_PATH"
check_if_directory_exists "$AFTER_QUERIES_PATH"

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

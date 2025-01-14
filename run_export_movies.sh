#!/bin/bash

CONFIGURATION_FILE="./params.config"
SCRIPT_DIR=$(dirname "$0")
BEFORE_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/Before"
AFTER_QUERIES_PATH="$SCRIPT_DIR/docker/Queries/Insert/After"
UPDATE_QUERY_PATH="$SCRIPT_DIR/docker/Queries/UpdatedData/insert_query.sql"

# Checks if the file exists
if [ ! -e "$CONFIGURATION_FILE" ]; then
    echo "The configuration file '$CONFIGURATION_FILE' does not exist."
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


"$JAVA_PATH" -jar "$JAR_PATH"

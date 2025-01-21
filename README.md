## **Movies metadata extractor**
Java tool that extracts movie metadata from TMDB API to a postgres database
## **Introduction**
The tool was created in order to use with any movie-related app that might need metadata for movies like revenue, budget, cast, crew or for use in any data analytics for movies.


- Java, runs the basic functions like extracting data from TMDB and from the Python API below.
- Python API using flask that runs in a docker container, uses beautiful soup to extract data from IMDB.
- Postgres database that runs in a docker container.
- Grafana that runs in a docker container, with some basic dashboards that show some basic functionalities for the tool

## **Requirements**
Java 17+

Docker and docker-compose

## **Installation**
To run the tool, follow the below instructions

1. Clone the repository: **`git clone https://github.com/geodumit/Movies-metadata-extractor.git`**
2. Start the docker containers: `./docker/start_docker_container.sh`
## **Usage**


## **License**

## **Authors and Acknowledgment**

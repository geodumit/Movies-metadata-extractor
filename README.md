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
2. Get an free API key from TMDB from https://www.themoviedb.org/login?to=read_me&redirect=%2Freference%2Fintro%2Fgetting-started
3. Edit params.config and add your parameters
   - api.key=The api key your got from step 2
   - batch.limit=default is 50, data of movies to store in memory before ingesting them in the database
   - movies.popularity=default is 3.5, with this parameter you can set how many movies to get. 3.5 is aproxximately 100000 movies.
   - java.path=your java path
   - jar.path=full path of the jar created when building the project
5. Start the docker containers: `./docker/start_docker_container.sh`
## **Usage**


## **License**

## **Authors and Acknowledgment**

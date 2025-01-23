## **Movies metadata extractor**
Java tool that extracts movie metadata from TMDB API to a postgres database
## **Introduction**
The tool was created in order to use with any movie-related app that might need metadata for movies like revenue, budget, cast, crew or for use in any data analytics for movies.


- Java, runs the basic functions like extracting data from TMDB and from the Python API below.
- Python API using flask that runs in a docker container, uses beautiful soup to extract data from IMDB.
- Postgres database that runs in a docker container.
- Grafana that runs in a docker container, with some basic dashboards that show some basic functionalities for the tool

High level of the architecture
![image](https://github.com/user-attachments/assets/3a9972ff-46cc-46a4-920c-9b49a0318b68)


## **Requirements**
Java 17+\
Docker and docker-compose\
Linux system or WSL on windows

## **Installation**
To run the tool, follow the below instructions

1. Clone the repository: **`git clone https://github.com/geodumit/Movies-metadata-extractor.git`**
2. run `mvn clean install`
3. Get a free API key from TMDB from https://www.themoviedb.org/login?to=read_me&redirect=%2Freference%2Fintro%2Fgetting-started
4. Edit params.config and add your parameters
   - api.key=The api key your got from step 2
   - batch.limit=default is 50, data of movies to store in memory before ingesting them in the database
   - movies.popularity=default is 3.5, with this parameter you can set how many movies to get. 3.5 is aproxximately 100000 movies.
   - java.path=your java path
   - jar.path=full path of the jar created when building the project (you can find the latest jar at target/Export_movies-1.0-SNAPSHOT-jar-with-dependencies.jar or build it on your own)
5. Start the docker containers: `./docker/start_docker_container.sh`. Three containers will start up, postgres, grafana and the python IMDB scraper

## **Usage**
After following the instructions in Installation you can run the tool by running
`./run_export_movies.sh`

The process will start, at the end of each batch it will show estimated time for the extraction of all the movies. For a popularity of 3.5(default) approximate time is 12 hours and 100k movies.

## **Database**
The database has default port 5432 and runs in docker container. Default port can be changed by editing **docker/docker-compose.yml** and **db.properties**\
After each batch is finished the below tables will be filled with data
   - *raw_details_metadata*: Raw data of data returned from TMDB details API
   - *raw_credits_metadata*: Raw data of data returned from TMDB credits API
   - *updated_data*: This table is used in order to track if each table has been updated for each id
   - *movies*: has data for each movie like title, release_date, budget, revenue, imdb rating, overview etc
   - *production_companies*: has data for the production companies like name, origin_country
   - *movie_production_companies*: used for joining movies and production_companies, has movie_id and production_id
   - *genres*: has data for the genres
   - *movie_genres*: used for joining movies and genres, has movie_id and genre_id
   - *actors*: has data for the actors like name, profile_path
   - *actor_characters*: had data for the characters played in movies
   - *crew*: similar to actors but for the rest of the crew
   - *crew_roles*: similar to actor_characters but for the crew

## **Grafana**
Grafana is used in order to show some cases of usage\
It has the postgres datasource and some dashboards 
   - *Individual movie* dashboard, shows basic information about a movie, the movie can be changed from the variable movie. The link in the actors redirects to the Individual actor below
     ![image](https://github.com/user-attachments/assets/b45cacab-8a4f-4034-96ea-33bf509f8c5b)
   - *Individual actor* dashboard, shows characters played by the actor, the actor can be changed from the variable actor. The link in the movie redirects to the Individual movie above
     ![image](https://github.com/user-attachments/assets/a33c4c05-334c-4a54-bfd2-ae0c83532d63)
   - *Movie Statistics* dashboards, basic graphs that show some statistics of movies
     ![image](https://github.com/user-attachments/assets/4603db64-5f92-4d6e-aaf1-0e77c3b5799f)



## **License**
MIT lincense can be found in LINCENSE.md

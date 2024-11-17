-- Stores the the raw data returned from the details api
CREATE TABLE raw_details_metadata (
    details_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	adult VARCHAR,
	backdropPath VARCHAR,
	collection VARCHAR,
	budget VARCHAR,
	genres TEXT,
	homepage VARCHAR,
	id VARCHAR,
	imdbId VARCHAR,
	originCountry VARCHAR,
	originalLanguage VARCHAR,
	originalTitle VARCHAR,
	overview TEXT,
	popularity VARCHAR,
	posterPath VARCHAR,
	productionCompanies TEXT,
	productionCountries TEXT,
	releaseDate VARCHAR,
	revenue VARCHAR,
	runtime VARCHAR,
	spokenLanguages TEXT,
	status VARCHAR,
	tagline VARCHAR,
	title VARCHAR,
	video VARCHAR,
	voteAverage VARCHAR,
	vote_count VARCHAR	,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores the raw data returned from the credits api
CREATE TABLE raw_credits_metadata (
	credits_id INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
	id VARCHAR,
	_cast TEXT,
	crew TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores the 1:1 data for each movie
CREATE TABLE movies (
    id INT primary key,
    adult boolean,
    title VARCHAR,
    original_title VARCHAR,
    release_date DATE,
    IMDBid INT,
    backdrop_path VARCHAR,
    budget INT,
    original_language VARCHAR,
    overview text,
    popularity DECIMAL,
    poster_path VARCHAR,
    revenue INT,
    runtime INT,
    tagline VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stores the possible genres
CREATE TABLE genres (
    id INT PRIMARY KEY,
    name VARCHAR NOT NULL
);

-- Stores the genres for each movie
create table movie_genres (
	movie_id INT,
	genre_id Int,
	foreign key (movie_id) references movies(id),
	foreign key (genre_id) references genres(id)
);

-- Stores the possible production companies
create table production_companies (
	id INT primary key,
	name VARCHAR,
	origin_country VARCHAR
);

-- Stores the production companies for each movie
create table movie_production_companies (
	movie_id INT,
	production_id Int,
	foreign key (movie_id) references movies(id),
	foreign key (production_id) references production_companies(id)
);

-- Stores the characters played by the actors
create table actor_characters (
	movie_id INT,
	actor_id INT,
	character varchar
)

-- Stores the actors
create table actors (
	id INT primary key,
	name varchar,
	known_for_department varchar,
	profile_path varchar
)

-- Stores the crew roles for each movie
create table crew_roles (
	movie_id INT,
	crew_id INT,
	department varchar,
	job varchar
)

-- Stores the crew
create table crew (
	id int primary key,
	name varchar,
	known_for_department varchar,
	profile_path varchar
)

-- Stores if data has been updated for each movie id for each table
create table updated_data (
	movie_id INT primary key,
	movies_updated boolean,
	movie_genres_updated boolean,
	movie_production_companies_updated boolean,
	actor_characters_updated boolean,
	crew_roles_updated boolean
)
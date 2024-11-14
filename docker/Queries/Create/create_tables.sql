CREATE TABLE raw_details_metadata (
    details_id SERIAL PRIMARY KEY,
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

CREATE TABLE raw_credits_metadata (
	credits_id SERIAL PRIMARY KEY,
	id VARCHAR,
	_cast TEXT,
	crew TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movies (
    id INT primary key,
    title VARCHAR NOT NULL,
    release_date DATE,
    backdrop_path VARCHAR,
    budget INT,
    original_language VARCHAR,
    overview text,
    popularity DECIMAL,
    poster_path VARCHAR,
    revenue INT,
    runtime INT,
    status VARCHAR,
    tagline VARCHAR,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genres (
    id INT PRIMARY KEY,
    name VARCHAR NOT NULL
);

create table movie_genres (
	movie_id INT,
	genre_id Int,
	foreign key (movie_id) references movies(id),
	foreign key (genre_id) references genres(id)
);

create table production_companies (
	id INT primary key,
	name VARCHAR,
	origin_country VARCHAR
);

create table movie_production_companies (
	movie_id INT,
	production_id Int,
	foreign key (movie_id) references movies(id),
	foreign key (production_id) references production_companies(id)
);

create table actor_characters (
	movie_id INT,
	actor_id INT,
	character varchar
)

create table actors (
	id INT primary key,
	name varchar,
	known_for_department varchar,
	profile_path varchar
)

create table crew_roles (
	movie_id INT,
	crew_id INT,
	department varchar,
	job varchar
)

create table crew (
	id int primary key,
	name varchar,
	known_for_department varchar,
	profile_path varchar
)
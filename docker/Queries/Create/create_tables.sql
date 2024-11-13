CREATE TABLE raw_details_metadata (
    details_id SERIAL PRIMARY KEY,
	adult VARCHAR(255),
	backdropPath VARCHAR(255),
	collection VARCHAR(255),
	budget VARCHAR(255),
	genres TEXT,
	homepage VARCHAR(255),
	id VARCHAR(255),
	imdbId VARCHAR(255),
	originCountry VARCHAR(255),
	originalLanguage VARCHAR(255),
	originalTitle VARCHAR(255),
	overview TEXT,
	popularity VARCHAR(255),
	posterPath VARCHAR(255),
	productionCompanies TEXT,
	productionCountries TEXT,
	releaseDate VARCHAR(255),
	revenue VARCHAR(255),
	runtime VARCHAR(255),
	spokenLanguages TEXT,
	status VARCHAR(255),
	tagline VARCHAR(255),
	title VARCHAR(255),
	video VARCHAR(255),
	voteAverage VARCHAR(255),
	vote_count VARCHAR(255)	,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE raw_credits_metadata (
	credits_id SERIAL PRIMARY KEY,
	id VARCHAR(255),
	_cast TEXT,
	crew TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE movies (
    id INT primary key,
    title VARCHAR(255) NOT NULL,
    release_date DATE,
    backdrop_path VARCHAR(255),
    budget INT,
    original_language VARCHAR(255),
    overview text,
    popularity DECIMAL,
    poster_path VARCHAR(255),
    revenue INT,
    runtime INT,
    status VARCHAR(255),
    tagline VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE genres (
    id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

create table movie_genres (
	movie_id INT,
	genre_id Int,
	foreign key (movie_id) references movies(id),
	foreign key (genre_id) references genres(id)
);

create table production_companies (
	id INT primary key,
	name VARCHAR(255),
	origin_country VARCHAR(255)
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
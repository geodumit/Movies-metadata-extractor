insert into updated_data
select cast(id as integer), false , false, false, false, false,false, false, false from raw_details_metadata
on conflict(movie_id) do nothing

-- Insert data from raw_details_metadata to movies
insert into movies (id, adult , title, original_title, release_date, imdbid, backdrop_path, budget, original_language, overview, popularity, poster_path, revenue, runtime, tagline)
select
	cast(id as INT),
	cast(adult as Boolean),
	title,
	originaltitle,
	TO_DATE(releasedate, 'YYYY-MM-DD'),
	imdbid,
	backdroppath,
	cast(budget as INT),
	originallanguage,
	overview,
	cast(popularity as DECIMAL),
	posterpath,
	cast(revenue as INT),
	cast(runtime as INT),
	tagline
from raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movies_updated = false)
on conflict (id) do nothing

update updated_data
set movies_updated = true
where movie_id in
(select id from movies)


-- Insert data from raw_details_metadata to genres
insert into genres
select distinct
	cast(jsonb_array_elements(genres::jsonb)->>'id' AS integer) as id,
	jsonb_array_elements(genres::jsonb)->>'name' as name
from
	raw_details_metadata rdm
on conflict(id) do nothing


insert into movie_genres ( movie_id, genre_id)
SELECT
	cast(id as integer),
	cast(jsonb_array_elements(genres::jsonb)->>'id' AS integer) as id
FROM 
    raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movie_genres_updated = false)

update updated_data
set movie_genres_updated = true
where movie_id in
(select distinct movie_id from movie_genres)



insert into production_companies (name, id, origin_country)
select DISTINCT
    jsonb_array_elements(productioncompanies::jsonb)->>'name' AS name,
	cast(jsonb_array_elements(productioncompanies::jsonb)->>'id' AS integer) as id,
	jsonb_array_elements(productioncompanies::jsonb)->>'origin_country' AS origin_country
FROM 
    raw_details_metadata
on conflict(id) do nothing


insert into movie_production_companies
select
    cast(id as integer),
	cast(jsonb_array_elements(productioncompanies::jsonb)->>'id' AS integer) as id
FROM
    raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movie_production_companies_updated = false)

update updated_data
set movie_production_companies_updated = true
where movie_id in
(select movie_id from movie_production_companies)

insert into actor_characters (movie_id, actor_id, character)
select 
	cast(id as integer), 
	cast(jsonb_array_elements(_cast::jsonb)->>'id' as integer) AS id,
	jsonb_array_elements(_cast::jsonb)->>'character' AS character
from 
	raw_credits_metadata

insert into crew_roles (movie_id, crew_id, department, job)
select 
	cast(id as integer) as movie_id, 
	cast(jsonb_array_elements(crew::jsonb)->>'id' as integer) AS crew_id,
	jsonb_array_elements(crew::jsonb)->>'department' AS department,
	jsonb_array_elements(crew::jsonb)->>'job' AS job
from 
	raw_credits_metadata

insert into crew (id, name, known_for_department, profile_path)
select distinct
	cast(jsonb_array_elements(crew::jsonb)->>'id' as integer) AS id,
	jsonb_array_elements(crew::jsonb)->>'original_name' AS original_name,
	jsonb_array_elements(crew::jsonb)->>'known_for_department' AS known_for_department,
	jsonb_array_elements(crew::jsonb)->>'profile_path' AS profile_path
from 
	raw_credits_metadata
on conflict (id) do nothing

insert into actors (id, name, known_for_department, profile_path)
select distinct
	cast(jsonb_array_elements(_cast::jsonb)->>'id' as integer) AS id,
	jsonb_array_elements(_cast::jsonb)->>'original_name' AS original_name,
	jsonb_array_elements(_cast::jsonb)->>'known_for_department' AS known_for_department,
	jsonb_array_elements(_cast::jsonb)->>'profile_path' AS profile_path
from 
	raw_credits_metadata
on conflict (id) do nothing


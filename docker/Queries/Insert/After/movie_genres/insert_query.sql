insert into movie_genres ( movie_id, genre_id)
SELECT
	cast(id as integer),
	cast(jsonb_array_elements(genres::jsonb)->>'id' AS integer) as id
FROM 
    raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movie_genres_updated = false)
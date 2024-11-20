update updated_data
set movie_genres_updated = true
where movie_id in
(select distinct movie_id from movie_genres)
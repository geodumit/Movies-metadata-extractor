update updated_data
set movies_updated = true
where movie_id in
(select id from movies)
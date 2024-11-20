update updated_data
set actor_characters_updated = true
where movie_id in
(select movie_id from actor_characters)
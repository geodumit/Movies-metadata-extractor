insert into actor_characters
select
	cast(id as integer),
	cast(jsonb_array_elements(_cast::jsonb)->>'id' as integer) AS id,
	jsonb_array_elements(_cast::jsonb)->>'character' AS character
from
	raw_credits_metadata
where cast(id as integer) in
(select movie_id from updated_data
where actor_characters_updated = false)
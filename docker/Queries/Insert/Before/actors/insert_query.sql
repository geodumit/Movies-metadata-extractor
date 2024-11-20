insert into actors (id, name, known_for_department, profile_path)
select distinct
	cast(jsonb_array_elements(_cast::jsonb)->>'id' as integer) AS id,
	jsonb_array_elements(_cast::jsonb)->>'original_name' AS original_name,
	jsonb_array_elements(_cast::jsonb)->>'known_for_department' AS known_for_department,
	jsonb_array_elements(_cast::jsonb)->>'profile_path' AS profile_path
from 
	raw_credits_metadata
on conflict (id) do nothing
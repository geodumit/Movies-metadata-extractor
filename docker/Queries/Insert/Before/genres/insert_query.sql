insert into genres
select distinct
	cast(jsonb_array_elements(genres::jsonb)->>'id' AS integer) as id,
	jsonb_array_elements(genres::jsonb)->>'name' as name
from
	raw_details_metadata rdm
on conflict(id) do nothing
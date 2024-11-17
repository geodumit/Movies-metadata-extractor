insert into crew_roles
select
	cast(id as integer) as movie_id,
	cast(jsonb_array_elements(crew::jsonb)->>'id' as integer) AS crew_id,
	jsonb_array_elements(crew::jsonb)->>'department' AS department,
	jsonb_array_elements(crew::jsonb)->>'job' AS job
from
	raw_credits_metadata
where cast(id as integer) in
(select movie_id from updated_data
where crew_roles_updated = false)
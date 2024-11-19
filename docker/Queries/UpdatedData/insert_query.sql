insert into updated_data
select cast(rdm.id as integer), false, false, false, false, false, false, false, false, false 
from raw_details_metadata rdm 
left join updated_data ud
on cast(rdm.id as integer) = ud.movie_id 
where ud.movie_id is null
on conflict(movie_id) do nothing
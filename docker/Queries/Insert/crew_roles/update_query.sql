update updated_data
set crew_roles_updated = true
where movie_id in
(select movie_id from crew_roles)
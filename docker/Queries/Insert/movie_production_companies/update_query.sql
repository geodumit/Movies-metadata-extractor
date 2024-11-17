update updated_data
set movie_production_companies_updated = true
where movie_id in
(select movie_id from movie_production_companies)
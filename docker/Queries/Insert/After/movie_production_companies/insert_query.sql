insert into movie_production_companies
select
    cast(id as integer),
	cast(jsonb_array_elements(productioncompanies::jsonb)->>'id' AS integer) as id
FROM
    raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movie_production_companies_updated = false)
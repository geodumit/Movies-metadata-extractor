insert into production_companies (name, id, origin_country)
select DISTINCT
    jsonb_array_elements(productioncompanies::jsonb)->>'name' AS name,
	cast(jsonb_array_elements(productioncompanies::jsonb)->>'id' AS integer) as id,
	jsonb_array_elements(productioncompanies::jsonb)->>'origin_country' AS origin_country
FROM 
    raw_details_metadata
on conflict(id) do nothing
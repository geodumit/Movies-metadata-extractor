insert into movies (id, adult , title, original_title, release_date, imdbid, backdrop_path, budget, original_language, overview, popularity, poster_path, revenue, runtime, tagline)
select
	cast(id as INT),
	cast(adult as Boolean),
	title,
	originaltitle,
	TO_DATE(releasedate, 'YYYY-MM-DD'),
	imdbid,
	backdroppath,
	cast(budget as INT),
	originallanguage,
	overview,
	cast(popularity as DECIMAL),
	posterpath,
	cast(revenue as INT),
	cast(runtime as INT),
	tagline
from raw_details_metadata
where cast(id as integer) in
(select movie_id from updated_data
where movies_updated = false)
on conflict (id) do nothing
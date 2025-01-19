from bs4 import BeautifulSoup
import requests

headers={'User-Agent':'Mozilla/5.0 (Windows NT 6.3; Win 64 ; x64) Apple WeKit /537.36(KHTML , like Gecko) Chrome/80.0.3987.162 Safari/537.36'}

url = 'https://www.imdb.com/title/tt0371746/'

response = requests.get(url, headers = headers)


soup = BeautifulSoup(response.content, 'html.parser')

rating_element = soup.find('div', {'data-testid': 'hero-rating-bar__aggregate-rating__score'})

if rating_element:
    rating = rating_element.get_text(strip=True)
    print('IMDb Rating:', rating)
else:
    print('Rating not found.')

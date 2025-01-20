from bs4 import BeautifulSoup
import requests
from typing import Dict, Any
import logging
from models import Rating
from config import Config

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class IMDBScraper:
    def __init__(self, config: Config):
        self.config = config
        self.headers = {'User-Agent': config.USER_AGENT}
        
    def validate_imdb_id(self, imdb_id: str) -> bool:
        """Validate IMDB ID format."""
        return imdb_id.startswith('tt') and len(imdb_id) >= 7 and imdb_id[2:].isdigit()

    def scrape_score(self, imdb_id: str) -> Rating:
        """Scrape IMDB rating for a given movie ID."""
        if not self.validate_imdb_id(imdb_id):
            return Rating(error="Invalid IMDB ID format")

        url = f'https://www.imdb.com/title/{imdb_id}'
        
        try:
            response = requests.get(
                url, 
                headers=self.headers, 
                timeout=self.config.TIMEOUT
            )
            response.raise_for_status()
            
            if not response.content:
                return Rating(error="Empty response from server")

            soup = BeautifulSoup(response.content, 'html.parser')
            rating_element = soup.find(
                'div', 
                {'data-testid': 'hero-rating-bar__aggregate-rating__score'}
            )
            
            if rating_element is None:
                return Rating(error="Rating element not found")

            rating_details = rating_element.parent
            *_, rating_count = rating_details.children
            rating = next(rating_element.children, None)

            if rating and rating.text.strip():
                return Rating(
                    rating=rating.text.strip(),
                    rating_count=rating_count.text.strip()
                )
            return Rating(error="Rating not found")

        except requests.exceptions.Timeout:
            logger.error(f"Timeout accessing {url}")
            return Rating(error="Request timed out")
        except requests.exceptions.RequestException as e:
            logger.error(f"Network error accessing {url}: {str(e)}")
            return Rating(error=f"Network error: {str(e)}")
        except Exception as e:
            logger.error(f"Unexpected error processing {url}: {str(e)}")
            return Rating(error=f"Internal error: {str(e)}")
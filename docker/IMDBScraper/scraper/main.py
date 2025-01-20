from flask import Flask, jsonify
from imdb_scraper import IMDBScraper
from config import Config
import logging

app = Flask(__name__)
config = Config()
imdb_scraper = IMDBScraper(config)

@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint."""
    return jsonify({"status": "healthy"}), 200

@app.route('/rating/<string:imdb_id>', methods=['GET'])
def get_rating(imdb_id: str):
    """Get rating for a given IMDB ID."""
    result = imdb_scraper.scrape_score(imdb_id)
    if result.error:
        return jsonify({"error": result.error}), 404
    return jsonify({
        "rating": result.rating,
        "rating_count": result.rating_count
    }), 200

if __name__ == '__main__':
    logging.info(f"Starting server on {config.HOST}:{config.PORT}")
    app.run(
        host=config.HOST,
        port=config.PORT,
        debug=config.DEBUG
    )
# Use a lightweight Python base image
FROM python:3.12-slim

# Set the working directory
WORKDIR /app

RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*

# Copy the application code to the container
COPY scraper/main.py /app
COPY scraper/imdb_scraper.py /app
COPY scraper/models.py /app
COPY scraper/config.py /app
COPY scraper/requirements.txt /app

# Install dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Expose the port the app runs on
EXPOSE 5000

# Command to run the application
CMD ["python", "main.py"]

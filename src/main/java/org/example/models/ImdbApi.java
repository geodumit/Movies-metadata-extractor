package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ImdbApi {

    private static final Logger logger = LogManager.getLogger(TMDBAPI.class);

    public ImdbApi() {

    }

    private String makeRequest(String URL) {
        HttpResponse<String> response = null;
        String errorMessage = "";
        boolean requestFailed = false;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .header("Accept", "application/json")
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                errorMessage = String.valueOf(response.statusCode());
                requestFailed =  true;
            }

        } catch (Exception e) {
            errorMessage = e.getMessage();
            requestFailed =  true;
        }

        if (requestFailed) {
            logger.warn("Something went wrong with the request {}", errorMessage);
            return null;
        } else {
            return response.body();
        }
    }

    public String getRating(String imdbID) {
        // Must get the url and port from arguments
        String URL = "http://localhost:5000/rating/" + imdbID;
        return makeRequest(URL);
    }
}

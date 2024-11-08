package org.example.models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class TMDBAPI {
    String token;

    private static final Logger logger = LogManager.getLogger(TMDBAPI.class);

    public TMDBAPI(String token) {
        this.token = "Bearer " + token;
    }

    public String getDetails(int id) {
        String URL = "https://api.themoviedb.org/3/movie/" + id + "?language=en-US";
        HttpResponse<String> response;

        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(URL))
                    .header("Accept", "application/json")
                    .header("Authorization", token)
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                logger.debug("Response: {}", response.body());
            } else {
                logger.warn("Request failed with status code: {}", response.statusCode());
                return null;
            }
        } catch (Exception e) {
            logger.warn("Something went wrong with the request {}", e.getMessage());
            return null;
        }

        return response.body();
    }
}

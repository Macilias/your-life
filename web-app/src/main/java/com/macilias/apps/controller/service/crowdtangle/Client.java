package com.macilias.apps.controller.service.crowdtangle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class Client {

    private final String apiToken;

    public Client(String apiToken) {

        this.apiToken = apiToken;
    }

    public JsonObject get(URL url) throws IOException {
        URLConnection con = url.openConnection();

        HttpURLConnection http = (HttpURLConnection) con;

        http.setRequestMethod("GET");
        http.setRequestProperty("x-api-token", apiToken);
        http.setDoOutput(true);
        http.connect();

        int responseCode = http.getResponseCode();

        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Request failed with HTTP response code " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        String inputLine;
        StringBuffer response;
        response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        Gson gson = new GsonBuilder().create();

        return gson.fromJson(response.toString(), JsonObject.class);
    }
}

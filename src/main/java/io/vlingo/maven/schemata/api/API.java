// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.maven.schemata.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

public class API {

  private final io.vlingo.actors.Logger logger = io.vlingo.actors.Logger.basicLogger();

  public void post(final String type,
                   final URL baseURL,
                   final String route,
                   final Object payload) throws IOException, MojoExecutionException {
    URL url = resolveUrl(baseURL, route);

    logger.info("Pushing {} to {}.", type, url);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");

    OutputStream os = connection.getOutputStream();
    os.write(new Gson().toJson(payload).getBytes(StandardCharsets.UTF_8));
    os.close();

    if (connection.getResponseCode() == HTTP_CREATED) {
      logger.info("Successfully pushed {}", url);
    } else {
      logError(connection, "Pushing the schema version failed: {}");
      throw new MojoExecutionException(
              "Could not push " + type
                      + " to " + url
                      + ": " + connection.getResponseMessage()
                      + " - " + connection.getResponseCode());
    }
  }

  public <T> List<T> getAll(final Class<T[]> type,
                            final URL baseURL,
                            final String route) throws IOException, MojoExecutionException {
    try {
      waitQueryServiceReadiness(); //Due to eventual consistency
    } catch (final InterruptedException e) {
      e.printStackTrace();
    }

    URL url = resolveUrl(baseURL, route);

    logger.info("Querying {} from {}.", type.getSimpleName(), url);

    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setRequestMethod("GET");

    if (connection.getResponseCode() == HTTP_OK) {
      return Arrays.asList(new Gson().fromJson(response(connection), type));
    } else {
      logError(connection, "Querying " + type.getSimpleName() + " failed: {}");
      throw new MojoExecutionException("Could not get " + type + " from " + url + ": "
              + connection.getResponseMessage() + " - " + connection.getResponseCode() +
              "-" + response(connection));
    }
  }

  private void logError(final HttpURLConnection connection, final String pattern) throws IOException {
    try (BufferedReader br = new BufferedReader(
            new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
      String responseLine;
      StringBuilder response = new StringBuilder();
      while ((responseLine = br.readLine()) != null) {
        response.append(responseLine.trim());
      }
      logger.error(pattern, response.toString());
    }
  }

  private URL resolveUrl(final URL baseUrl, final String route) throws MalformedURLException {
    return new URL(baseUrl, "api/" + route);
  }

  private String response(final HttpURLConnection connection) throws IOException {
    return new BufferedReader(
            new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))
            .lines().collect(Collectors.joining("\n"));
  }

  private void waitQueryServiceReadiness() throws InterruptedException {
    Thread.sleep(1500);
  }
}

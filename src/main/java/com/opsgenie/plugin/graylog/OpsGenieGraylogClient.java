package com.opsgenie.plugin.graylog;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opsgenie.plugin.graylog.alertapi.CreateAlertRequest;
import com.opsgenie.plugin.graylog.alertapi.CreateAlertResponse;

import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

class OpsGenieGraylogClient {

    private static final String ALERT_API_URL = "https://api.opsgenie.com/v2/alerts";

    private final Logger logger = LoggerFactory.getLogger(OpsGenieGraylogClient.class);

    private String apiKey;
    private String tags;
    private String teams;
    private String priority;
    private ObjectMapper objectMapper;

    OpsGenieGraylogClient(String apiKey, String tags, String teams, String priority) {
        this.apiKey = apiKey;
        this.tags = tags;
        this.teams = teams;
        this.priority = priority;

        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    void trigger(Stream stream, AlertCondition.CheckResult checkResult) throws AlarmCallbackException {
        final URL url = generateUrl();

        final HttpURLConnection connection = generateConnection(url);

        try (final OutputStream requestStream = connection.getOutputStream()) {
            final CreateAlertRequest createAlertRequest = generateRequest(stream, checkResult);
            sendRequest(requestStream, createAlertRequest);

            if (isSuccess(connection)) {
                handleSuccessResponse(connection);
            } else {
                handleFailure(connection);
            }

        } catch (IOException e) {
            logger.error("Could not POST alert to OpsGenie API.", e);
            throw new AlarmCallbackException("Could not POST alert to OpsGenie API.", e);
        }
    }

    private URL generateUrl() throws AlarmCallbackException {
        final URL url;

        try {
            url = new URL(ALERT_API_URL);
        } catch (MalformedURLException e) {
            logger.error("Malformed URL for OpsGenie Alert API", e);
            throw new AlarmCallbackException("Malformed URL for OpsGenie Alert API", e);
        }
        return url;
    }

    private HttpURLConnection generateConnection(URL url) throws AlarmCallbackException {
        final HttpURLConnection connection;

        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            logger.error("Error while opening connection to OpsGenie Alert API.", e);
            throw new AlarmCallbackException("Error while opening connection to OpsGenie Alert API.", e);
        }

        connection.setDoOutput(true);
        connection.setRequestProperty("Authorization", "GenieKey " + apiKey);
        connection.setRequestProperty("User-Agent", "opsgenie-graylog-plugin/" + new OpsGenieAlarmCallbackMetaData().getVersion().toString());
        connection.setRequestProperty("Accept", "application/json");
        try {
            connection.setRequestMethod("POST");
        } catch (ProtocolException e) {
            logger.error("Could not POST alert to OpsGenie API.", e);
            throw new AlarmCallbackException("Could not POST alert to OpsGenie API.", e);
        }
        connection.setRequestProperty("Content-Type", "application/json");
        return connection;
    }

    private CreateAlertRequest generateRequest(Stream stream, AlertCondition.CheckResult checkResult) {
        CreateAlertRequest request = new CreateAlertRequest();

        request.setTags(Arrays.asList(tags.split(",")));

        List<CreateAlertRequest.TeamRecipient> teams = new ArrayList<>();
        String[] teamNames = this.teams.split(",");
        for (String teamName : teamNames) {
            CreateAlertRequest.TeamRecipient recipient = new CreateAlertRequest.TeamRecipient();
            recipient.setName(teamName);
            teams.add(recipient);
        }
        request.setTeams(teams);

        request.setMessage(String.format("[Graylog] Stream: %s: %s", stream.getTitle(), checkResult.getResultDescription()));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(checkResult.getResultDescription()).append("\n")
                .append("Stream : ").append(stream.getDescription()).append("\n")
                .append("Trigger Condition: ").append(checkResult.getTriggeredCondition().getDescription()).append("\n");

        if (checkResult.getMatchingMessages().size() > 0) {
            stringBuilder.append("\nMatching messages: \n");
            for (MessageSummary summary : checkResult.getMatchingMessages()) {

                stringBuilder.append("Message: ").append(summary.getMessage()).append("\n")
                        .append("Source: ").append(summary.getSource()).append("\n")
                        .append("Stream Ids: ").append(summary.getStreamIds()).append("\n")
                        .append("Fields: ").append(summary.getFields()).append("\n\n")
                        .append("------------------").append("\n")
                ;

            }
        }
        request.setDescription(stringBuilder.toString().trim());

        Map<String, String> details = new HashMap<>();
        details.put("Triggered At", checkResult.getTriggeredAt().toString());
        details.put("Stream Id", stream.getId());
        details.put("Stream Title", stream.getTitle());
        request.setDetails(details);

        request.setPriority(priority);

        return request;
    }

    private void sendRequest(OutputStream requestStream, CreateAlertRequest createAlertRequest) throws IOException {
        byte[] body = objectMapper.writeValueAsBytes(createAlertRequest);
        requestStream.write(body);
        requestStream.flush();
    }

    private boolean isSuccess(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() == 200 || connection.getResponseCode() == 201 || connection.getResponseCode() == 202;
    }

    private void handleSuccessResponse(HttpURLConnection connection) throws IOException {
        InputStream responseStream = connection.getInputStream();
        CreateAlertResponse response = objectMapper.readValue(responseStream, CreateAlertResponse.class);
        logger.info("Successfully sent alert creation request to OpsGenie. Message: {} RequestId: {}", response.getResult(), response.getRequestId());
    }

    private void handleFailure(HttpURLConnection connection) throws AlarmCallbackException {
        InputStream responseStream;
        responseStream = connection.getErrorStream();
        Scanner s = new Scanner(responseStream).useDelimiter("\\A");
        String responseBody = s.hasNext() ? s.next() : "";
        logger.error("Error while creating alert at OpsGenie. Response: {}", responseBody);
        throw new AlarmCallbackException("Error while creating alert at OpsGenie. Response: " + responseBody);
    }


}

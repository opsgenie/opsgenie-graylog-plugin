package com.opsgenie.plugin.graylog;

import com.ifountain.opsgenie.client.OpsGenieClient;
import com.ifountain.opsgenie.client.OpsGenieClientException;
import com.ifountain.opsgenie.client.model.alert.CreateAlertRequest;
import org.graylog2.plugin.MessageSummary;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.alarms.callbacks.AlarmCallbackException;
import org.graylog2.plugin.streams.Stream;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class OpsGenieGraylogClient {
    private String apiKey;
    private String tags;
    private String recipients;
    private String teams;

    public OpsGenieGraylogClient(String apiKey, String tags, String recipients, String teams) {
        this.apiKey = apiKey;
        this.tags = tags;
        this.recipients = recipients;
        this.teams = teams;
    }

    public void trigger(Stream stream, AlertCondition.CheckResult checkResult) throws AlarmCallbackException {
        OpsGenieClient client = new OpsGenieClient();
        CreateAlertRequest alertRequest = createAlertRequest(stream, checkResult);
        try {
            client.alert().createAlert(alertRequest);
        } catch (OpsGenieClientException | IOException | ParseException e) {
            throw new AlarmCallbackException("An error occurred while creating OpsGenie alert", e);
        } finally {
            client.close();
        }
    }

    private CreateAlertRequest createAlertRequest(Stream stream, AlertCondition.CheckResult checkResult) {
        CreateAlertRequest request = new CreateAlertRequest();
        request.setApiKey(apiKey);
        request.setTags(Arrays.asList(tags.split(",")));
        request.setRecipients(Arrays.asList(recipients.split(",")));
        request.setTeams(Arrays.asList(teams.split(",")));

        request.setMessage(String.format("[Graylog] Stream: %s: %s", stream.getTitle(), checkResult.getResultDescription()));

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(checkResult.getResultDescription()).append("\n")
                .append("Stream : ").append(stream.getDescription()).append("\n")
                .append("Trigger Condition: ").append(checkResult.getTriggeredCondition().getDescription()).append("\n");

        if (checkResult.getMatchingMessages().size() > 0) {
            stringBuilder.append("\nMatching messages: \n");
            for (MessageSummary summary : checkResult.getMatchingMessages()) {
                stringBuilder.append(summary.getMessage()).append("\n");
            }
        }

        request.setDescription(stringBuilder.toString().trim());

        Map<String, String> details = new HashMap<>();
        details.put("Triggered At", checkResult.getTriggeredAt().toString());
        details.put("Stream Id", stream.getId());
        details.put("Stream Title", stream.getTitle());
        request.setDetails(details);

        return request;
    }
}

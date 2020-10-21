package com.opsgenie.plugin.graylog;

import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.ServerStatus;
import org.graylog2.plugin.Version;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class OpsGenieAlarmCallbackMetaData implements PluginMetaData {
    @Override
    public String getUniqueId() {
        return OpsGenieAlarmCallback.class.getCanonicalName();
    }

    @Override
    public String getName() {
        return "OpsGenie Alarm Callback";
    }

    @Override
    public String getAuthor() {
        return "OpsGenie";
    }

    @Override
    public URI getURL() {
        return URI.create("https://www.opsgenie.com/");
    }

    @Override
    public Version getVersion() {
        return new Version(1, 3, 9, "RELEASE");
    }

    @Override
    public String getDescription() {
        return "Graylog OpsGenie Integration plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(2, 3, 2);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}

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
        return "com.opsgenie.plugin.graylog.OpsGenieAlarmCallbackPlugin";
    }

    @Override
    public String getName() {
        return "OpsGenieAlarmCallback";
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
        return new Version(1, 0, 0);
    }

    @Override
    public String getDescription() {
        return "Graylog Opsgenie Integration plugin";
    }

    @Override
    public Version getRequiredVersion() {
        return new Version(1, 0, 0);
    }

    @Override
    public Set<ServerStatus.Capability> getRequiredCapabilities() {
        return Collections.emptySet();
    }
}

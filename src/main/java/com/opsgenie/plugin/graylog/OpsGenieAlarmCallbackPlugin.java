package com.opsgenie.plugin.graylog;

import org.graylog2.plugin.Plugin;
import org.graylog2.plugin.PluginMetaData;
import org.graylog2.plugin.PluginModule;

import java.util.Arrays;
import java.util.Collection;

public class OpsGenieAlarmCallbackPlugin implements Plugin {
    @Override
    public PluginMetaData metadata() {
        return new OpsGenieAlarmCallbackMetaData();
    }

    @Override
    public Collection<PluginModule> modules() {
        return Arrays.<PluginModule>asList(new OpsGenieAlarmCallbackModule());
    }
}

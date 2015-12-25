package com.opsgenie.plugin.graylog;

import org.graylog2.plugin.PluginConfigBean;
import org.graylog2.plugin.PluginModule;

import java.util.Collections;
import java.util.Set;

public class OpsGenieAlarmCallbackModule extends PluginModule {
    @Override
    public Set<? extends PluginConfigBean> getConfigBeans() {
        return Collections.emptySet();
    }

    @Override
    protected void configure() {
        addAlarmCallback(OpsGenieAlarmCallback.class);
    }
}

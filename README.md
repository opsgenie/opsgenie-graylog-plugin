# OpsGenie Graylog Integration Plugin

[OpsGenie](https://www.opsgenie.com) has a specific alert plugin for Graylog. Using this plugin, Graylog sends stream alerts to OpsGenie, with detailed information. OpsGenie acts as a dispatcher for Graylog alerts, determining the right people to notify based on on-call schedules, using email, text messages (SMS), phone calls and iPhone &amp; Android push notifications, and escalating alerts until the alert is acknowledged or closed.

Installing the Plugin
---------------
* [Download the plugin](https://app.opsgenie.com/download?tag=graylog).
* Copy the .jar file into your plugins directory, as [explained here](http://docs.graylog.org/en/latest/pages/plugins.html#installing-and-loading-plugins).
* You should give read permission to the .jar file. 
* Restart graylog-server.

Add API integration in OpsGenie
---------------
* Please [create an OpsGenie account](https://www.opsgenie.com/#signup) if you haven't done already
* Go to [Graylog Integration](https://app.opsgenie.com/integration?add=Graylog) page
* Specify the teams should be notified for Graylog alerts using the "Teams" field. Auto-complete suggestions will be provided as you type.
* Copy the integration API Key  by clicking on the copy button or selecting. You'll be using this in the Graylog configuration.
* Click "Save Integration".

Configuration on Graylog
---------------
* In Graylog, Go to Alert page and click "Manage notifications".
* Click "Add new notification".
* Select your desired stream.
* Under notification type, select **OpsGenie alarm callback** and click "Add notification".
* Paste the API key you copied into "OpsGenie API Key" field. And paste the API URL into the "OpsGenie API URL" field. You can optionally specify Teams and Tags here also.
* Click Save.

Building the Plugin
--------------
This project is using Maven 3 and requires Java 7 or higher. The plugin will require Graylog 1.0.0 or higher.

* Clone this repository.
* Run `mvn package` to build a JAR file.
* Optional: Run `mvn jdeb:jdeb` and `mvn rpm:rpm` to create a DEB and RPM package respectively.
* Copy generated JAR file in target directory to your Graylog plugin directory.
* Restart the Graylog.

package org.elasticsearch.river.eventstore;

import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.support.XContentMapValues;
import org.elasticsearch.river.RiverSettings;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreRiverConfig {
    public final String riverName;

    public final String host;
    public final int port;
    public final String userName;
    public final String password;
    public final String streamName;

    public final int bulkSize;
    public final TimeValue bulkTimeout;

    public EventStoreRiverConfig(String riverName, RiverSettings settings)
    {
        this.riverName = riverName;
        if (settings.settings().containsKey("eventstore")) {
            Map<String, Object> eventStoreSettings = (Map<String, Object>) settings.settings().get("eventstore");

            host = XContentMapValues.nodeStringValue(eventStoreSettings.get("host"), "localhost");
            port = XContentMapValues.nodeIntegerValue(eventStoreSettings.get("port"), 1113);

            streamName = XContentMapValues.nodeStringValue(eventStoreSettings.get("stream"), "EventCapture");

            userName = XContentMapValues.nodeStringValue(eventStoreSettings.get("username"), "admin");
            password = XContentMapValues.nodeStringValue(eventStoreSettings.get("password"), "changeit");
        }
        else
        {
            host = "localhost";
            port = 1113;
            streamName = "EventCapture";
            userName = "admin";
            password = "changeit";
        }

        if (settings.settings().containsKey("index")) {
            Map<String, Object> indexSettings = (Map<String, Object>) settings.settings().get("index");
            bulkSize = XContentMapValues.nodeIntegerValue(indexSettings.get("bulk_size_bytes"), 10*1024*1024);
            if (indexSettings.containsKey("bulk_timeout")) {
                bulkTimeout = TimeValue.parseTimeValue(XContentMapValues.nodeStringValue(indexSettings.get("bulk_timeout"), "10ms"), TimeValue.timeValueMillis(10000));
            } else {
                bulkTimeout = TimeValue.timeValueMillis(10);
            }
        } else {
            bulkSize = 10*1024*1024;
            bulkTimeout = TimeValue.timeValueMillis(10000);
        }
    }
}

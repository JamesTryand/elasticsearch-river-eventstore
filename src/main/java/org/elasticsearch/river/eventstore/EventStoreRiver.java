package org.elasticsearch.river.eventstore;

import akka.actor.ActorSystem;
import eventstore.Event;
import eventstore.EventData;
import eventstore.Settings;
import eventstore.SubscriptionObserver;
import eventstore.j.EsConnection;
import eventstore.j.EsConnectionFactory;
import eventstore.j.SettingsBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.type.TypeReference;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.river.AbstractRiverComponent;
import org.elasticsearch.river.River;
import org.elasticsearch.river.RiverName;
import org.elasticsearch.river.RiverSettings;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreRiver extends AbstractRiverComponent implements River {

    private final Client client;
    private final EventStoreRiverConfig riverConfig;
    private final ObjectReader reader = new ObjectMapper().reader(new TypeReference<Map<String, Object>>() {});

    private volatile Closeable closeable;

    @Inject
    public EventStoreRiver(RiverName riverName, RiverSettings settings, Client client) {
        super(riverName, settings);

        this.client = client;

        try {
            logger.info("EventStoreRiver created: name={}, type={}", riverName.getName(), riverName.getType());
            this.riverConfig = new EventStoreRiverConfig(riverName.getName(), settings);
        } catch (Exception e) {
            logger.error("Unexpected Error occurred", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start() {

        ActorSystem system = ActorSystem.create();
        Settings settings = new SettingsBuilder()
                .address(new InetSocketAddress(riverConfig.host, riverConfig.port))
                .defaultCredentials(riverConfig.userName, riverConfig.password)
                .build();

        EsConnection connection = EsConnectionFactory.create(system, settings);
        final MetadataAccess mda = new ElasticSearchMetadataAccessImpl(client);
        EventStoreRiverMetadata metadata;

        try {
            metadata = mda.retrieve();
        } catch (Exception e) {
            // TODO: Catch only missing index exception.
            logger.error("Cannot retrieve metadata", e);
            metadata = new EventStoreRiverMetadata();
        }

        closeable = connection.subscribeToStreamFrom(riverConfig.streamName, new SubscriptionObserver<Event>() {
            @Override
            public void onLiveProcessingStart(Closeable closeable) {
                logger.info("Starting eventstore subscription");
            }

            @Override
            public void onEvent(Event event, Closeable closeable) {

                int offset = 0;

                try {
                    EventData eventData = event.data();
                    offset = event.number().value();

                    Map<String, Object> metadata = reader.readValue(eventData.metadata().value().toArray());

                    // TODO: Bulk insert & update metadata
                    IndexRequestBuilder builder = client.prepareIndex((String)metadata.get("index"), eventData.eventType(), (String)metadata.get("id"))
                            .setSource(eventData.data().value().toArray())
                            .setTimestamp(String.valueOf(System.currentTimeMillis()));
                    builder.execute().actionGet();

                    try {
                        mda.update(new EventStoreRiverMetadata(offset));
                    } catch (Exception e) {
                        logger.error("Cannot update offset, current offset {}", e, offset);
                    }
                } catch (Exception e) {
                    logger.error("Cannot insert message into elasticsearch", e, offset);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                logger.error("Eventstore subscription error", throwable);
            }

            @Override
            public void onClose() {
                logger.info("Closing eventstore subscription");
            }
        }, metadata.offset, false, null);
    }

    @Override
    public void close() {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                logger.warn("Cannot close eventstore subscription", e);
            }

            closeable = null;
        }
    }
}

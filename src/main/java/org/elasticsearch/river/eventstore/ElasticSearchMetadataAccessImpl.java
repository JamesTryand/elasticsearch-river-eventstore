package org.elasticsearch.river.eventstore;

import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.get.GetField;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ElasticSearchMetadataAccessImpl implements MetadataAccess {

    private final Client client;

    public ElasticSearchMetadataAccessImpl(Client client) {

        this.client = client;
    }

    @Override
    public EventStoreRiverMetadata retrieve() throws ExecutionException, InterruptedException {

        GetResponse response = client.get(new GetRequest(".eventstore", "metadata", "1")).actionGet();

        EventStoreRiverMetadata metadata = new EventStoreRiverMetadata();

        Map<String, Object> source = response.getSource();

        if (source != null && source.containsKey("offset")) {
            metadata.offset = (Integer)source.get("offset");
        }

        return metadata;
    }

    @Override
    public void update(EventStoreRiverMetadata metadata) throws ExecutionException, InterruptedException, IOException {

        client.prepareIndex(".eventstore", "metadata", "1").setSource(jsonBuilder().startObject().field("offset", metadata.offset).endObject()).execute().actionGet();
    }
}

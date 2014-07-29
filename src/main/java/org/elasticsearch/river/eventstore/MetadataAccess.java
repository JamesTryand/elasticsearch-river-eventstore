package org.elasticsearch.river.eventstore;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MetadataAccess {

    public EventStoreRiverMetadata retrieve() throws ExecutionException, InterruptedException;

    public void update(EventStoreRiverMetadata metadata) throws ExecutionException, InterruptedException, IOException;
}

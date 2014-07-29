package org.elasticsearch.river.eventstore;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreRiverMetadata {
    public int offset;

    public EventStoreRiverMetadata() {
    }

    public EventStoreRiverMetadata(int offset) {
        this.offset = offset;
    }
}

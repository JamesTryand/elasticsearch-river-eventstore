package org.elasticsearch.plugin.river.eventstore;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.plugins.AbstractPlugin;
import org.elasticsearch.river.RiversModule;
import org.elasticsearch.river.eventstore.EventStoreRiverModule;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreRiverPlugin extends AbstractPlugin {

    @Inject
    public EventStoreRiverPlugin() {
    }

    @Override
    public String name() {
        return "river-eventstore";
    }

    @Override
    public String description() {
        return "River EventStore Plugin";
    }

    public void onModule(RiversModule module) {
        module.registerRiver("eventstore", EventStoreRiverModule.class);
    }
}

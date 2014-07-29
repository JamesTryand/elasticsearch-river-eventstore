package org.elasticsearch.river.eventstore;

import org.elasticsearch.common.inject.AbstractModule;
import org.elasticsearch.river.River;

/**
 * Created with IntelliJ IDEA.
 * User: gma
 * Date: 26/05/14
 * Time: 4:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class EventStoreRiverModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(River.class).to(EventStoreRiver.class).asEagerSingleton();
    }
}

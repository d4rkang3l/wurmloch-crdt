package com.netopyr.wurmloch.examples;

import com.netopyr.wurmloch.crdt.GCounter;
import com.netopyr.wurmloch.store.LocalCrdtStore;
import org.hamcrest.MatcherAssert;

import static org.hamcrest.Matchers.is;

public class GCounterExample {

    public static void main(String[] args) {

        // create two LocalCrdtStores and connect them
        final LocalCrdtStore crdtStore1 = new LocalCrdtStore();
        final LocalCrdtStore crdtStore2 = new LocalCrdtStore();
        crdtStore1.connect(crdtStore2);

        // create a G-Counter and find the according replica in the second store
        final GCounter replica1 = crdtStore1.createGCounter("ID_1");
        final GCounter replica2 = crdtStore2.findGCounter("ID_1").get();

        // increment both replicas of the counter
        replica1.increment();
        replica2.increment(2L);

        // the stores are connected, thus the replicas are automatically synchronized
        MatcherAssert.assertThat(replica1.get(), is(3L));
        MatcherAssert.assertThat(replica2.get(), is(3L));

        // disconnect the stores simulating a network issue, offline mode etc.
        crdtStore1.disconnect(crdtStore2);

        // increment both counters again
        replica1.increment(3L);
        replica2.increment(5L);

        // the stores are not connected, thus the changes have only local effects
        MatcherAssert.assertThat(replica1.get(), is(6L));
        MatcherAssert.assertThat(replica2.get(), is(8L));

        // reconnect the stores
        crdtStore1.connect(crdtStore2);

        // the counter is synchronized automatically and contains now the sum of all increments
        MatcherAssert.assertThat(replica1.get(), is(11L));
        MatcherAssert.assertThat(replica2.get(), is(11L));

    }
}
package org.drools.reliability.infinispan.example;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.PersistedSessionOption;

/**
 * Example class to demonstrate how to use RemoteCacheManager.
 *
 * See RemoteCacheManagerExample for more details.
 */
public class RemoteCacheManagerExampleAfterFailOver {

    public static void main(String[] args) {
        // Here we use the savedSessionId to retrieve the session. Explicitly 0 for now, but it could be different.
        KieSession session = RemoteCacheManagerExample.getKieSession(PersistedSessionOption.fromSession(0, PersistedSessionOption.Strategy.STORES_ONLY));

        try {
            System.out.println("fireAllRules");
            session.fireAllRules();
            Object results = session.getGlobal("results");
            System.out.println("results = " + results);
        } finally {
            session.dispose();
        }
    }
}

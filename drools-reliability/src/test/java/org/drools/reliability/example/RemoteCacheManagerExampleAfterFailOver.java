package org.drools.reliability.example;

import java.util.ArrayList;
import java.util.List;

import org.drools.reliability.CacheManager;
import org.drools.reliability.domain.Person;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;

/**
 * Example class to demonstrate how to use RemoteCacheManagerDelegate.
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

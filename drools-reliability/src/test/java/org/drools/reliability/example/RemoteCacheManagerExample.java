package org.drools.reliability.example;

import java.util.ArrayList;
import java.util.List;

import org.drools.reliability.CacheManagerFactory;
import org.test.domain.Person;
import org.jetbrains.annotations.NotNull;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.internal.utils.KieHelper;

/**
 * Example class to demonstrate how to use RemoteCacheManager.
 * <p>
 * Test with an Infinispan server running on localhost:11222
 * <p>
 * After running this example, you can run RemoteCacheManagerExampleAfterFailOver to see the results.
 * <p>
 * So the steps are:
 * docker run -p 11222:11222 -e USER="admin" -e PASS="secret" quay.io/infinispan/server:14.0
 * Run RemoteCacheManagerExample
 * Run RemoteCacheManagerExampleAfterFailOver
 */
public class RemoteCacheManagerExample {

    public static final String BASIC_RULE =
            "import " + Person.class.getCanonicalName() + ";" +
                    "global java.util.List results;" +
                    "rule X when\n" +
                    "  $s: String()\n" +
                    "  $p: Person( getName().startsWith($s) )\n" +
                    "then\n" +
                    "  results.add( $p.getName() );\n" +
                    "end";

    public static void main(String[] args) {
        KieSession session = getKieSession(PersistedSessionOption.newSession(PersistedSessionOption.Strategy.STORES_ONLY));

        long savedSessionId = session.getIdentifier();
        System.out.println("savedSessionId = " + savedSessionId);

        session.insert("M");
        session.insert(new Person("Mario", 40));

        //--- Simulate a crash
        System.out.println("Simulating a crash");
    }

    @NotNull
    public static KieSession getKieSession(PersistedSessionOption option) {
        System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_MODE, "REMOTE");
        System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_HOST, "localhost");
        System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_PORT, "11222");
        System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_USER, "admin");
        System.setProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_PASS, "secret");

        KieBase kbase = new KieHelper().addContent(BASIC_RULE, ResourceType.DRL).build();
        KieSessionConfiguration conf = KieServices.get().newKieSessionConfiguration();
        conf.setOption(option);
        KieSession session = kbase.newKieSession(conf, null);
        List<Object> results = new ArrayList<>();
        session.setGlobal("results", results);
        return session;
    }
}

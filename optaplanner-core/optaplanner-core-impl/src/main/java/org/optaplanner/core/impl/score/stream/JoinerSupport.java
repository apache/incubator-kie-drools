package org.optaplanner.core.impl.score.stream;

import java.util.Iterator;
import java.util.ServiceLoader;

public final class JoinerSupport {

    private static volatile JoinerService INSTANCE;

    public static JoinerService getJoinerService() {
        if (INSTANCE == null) {
            synchronized (JoinerSupport.class) {
                if (INSTANCE == null) {
                    Iterator<JoinerService> servicesIterator = ServiceLoader.load(JoinerService.class).iterator();
                    if (!servicesIterator.hasNext()) {
                        throw new IllegalStateException("Joiners not found.\n"
                                + "Maybe include org.optaplanner:optaplanner-constraint-streams dependency in your project?\n"
                                + "Maybe ensure your uberjar bundles META-INF/services from included JAR files?");
                    } else {
                        INSTANCE = servicesIterator.next();
                    }
                }
            }
        }
        return INSTANCE;
    }
}

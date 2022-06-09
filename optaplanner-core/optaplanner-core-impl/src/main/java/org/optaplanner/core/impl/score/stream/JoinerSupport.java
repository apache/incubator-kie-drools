package org.optaplanner.core.impl.score.stream;

import java.util.ServiceLoader;

public final class JoinerSupport {

    private static volatile JoinerService INSTANCE;

    public static JoinerService getJoinerService() {
        if (INSTANCE == null) {
            synchronized (JoinerSupport.class) {
                if (INSTANCE == null) {
                    INSTANCE = ServiceLoader.load(JoinerService.class)
                            .findFirst()
                            .orElseThrow(() -> new IllegalStateException("Joiners not found.\n"
                                    + "Maybe include org.optaplanner:optaplanner-constraint-streams dependency in your project?\n"
                                    + "Maybe ensure your uberjar bundles META-INF/services from included JAR files?"));
                }
            }
        }
        return INSTANCE;
    }
}

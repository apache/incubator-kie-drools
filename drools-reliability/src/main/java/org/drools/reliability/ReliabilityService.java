package org.drools.reliability;

import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class ReliabilityService {

    private final EmbeddedCacheManager cacheManager;

    public ReliabilityService() {
        cacheManager = new DefaultCacheManager();
    }

}

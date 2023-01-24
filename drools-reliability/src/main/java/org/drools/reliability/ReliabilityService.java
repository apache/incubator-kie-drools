package org.drools.reliability;

import org.drools.core.common.InternalFactHandle;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;

public class ReliabilityService {

    private final EmbeddedCacheManager cacheManager;
    private final Cache<Object, InternalFactHandle> cache;

    public ReliabilityService() {
        cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration("reliability", new ConfigurationBuilder().build());
        cache = cacheManager.getCache("reliability");
    }

    public ReliabilityService(String cacheName) {
        cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration(cacheName, new ConfigurationBuilder().build());
        cache = cacheManager.getCache(cacheName);
    }

    public Cache<Object, InternalFactHandle> getCache(){
        return this.cache;
    }

    public int cacheSize(){return cache.size();}

}

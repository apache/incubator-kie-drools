package org.drools.reliability;

import org.drools.core.common.InternalFactHandle;
import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.cache.SingleFileStoreConfigurationBuilder;

public class ReliabilityService {

    //private final EmbeddedCacheManager cacheManager;
    private final Cache<Object, InternalFactHandle> cache;

    public ReliabilityService() {
        /*cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration("reliability", this.configBuilder().build()); //new ConfigurationBuilder().build()
        cache = cacheManager.getCache("reliability");
        cacheManager.start();*/
        cache = CacheManager.INSTANCE.getOrCreateCache("reliability");
    }

    /*public ReliabilityService(String cacheName) {
        cacheManager = new DefaultCacheManager();
        cacheManager.defineConfiguration(cacheName, new ConfigurationBuilder().build());
        cache = cacheManager.getCache(cacheName);
    }*/

    public Cache<Object, InternalFactHandle> getCache(){
        return this.cache;
    }

    public int cacheSize(){return cache.size();}

    //
    private ConfigurationBuilder configBuilder(){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.persistence()
                .passivation(false)
                .addStore(SingleFileStoreConfigurationBuilder.class)
                .preload(true)
                .shared(false)
                .ignoreModifications(false)
                .purgeOnStartup(false)
                .location("/tmp/cache")
                .async()
                .enabled(true);

        return builder;
    }
}

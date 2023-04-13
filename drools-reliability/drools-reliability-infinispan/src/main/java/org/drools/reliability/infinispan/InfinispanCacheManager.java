package org.drools.reliability.infinispan;

import org.drools.reliability.core.TestableCacheManager;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.drools.reliability.core.CacheManagerFactory.RELIABILITY_CACHE_ALLOWED_PACKAGES;
import static org.drools.util.Config.getOptionalConfig;

public interface InfinispanCacheManager extends TestableCacheManager {
    void setRemoteCacheManager(RemoteCacheManager remoteCacheManager);

    void setEmbeddedCacheManager(DefaultCacheManager cacheManager);

    ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder();

    static String[] getAllowedPackages() {
        List<String> allowList = new ArrayList<>();
        allowList.add("org.kie.*");
        allowList.add("org.drools.*");
        allowList.add("java.*");
        getOptionalConfig(RELIABILITY_CACHE_ALLOWED_PACKAGES)
                .ifPresent(additionalPkgs -> Arrays.stream(additionalPkgs.split(",")).forEach(p -> allowList.add(p + ".*")));
        return allowList.toArray(new String[allowList.size()]);
    }
}

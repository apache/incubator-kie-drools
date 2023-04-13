package org.drools.reliability.core;

import org.kie.api.internal.utils.KieService;

public interface CacheManagerFactory extends KieService {

    String SESSION_CACHE_PREFIX = "session_";
    String SHARED_CACHE_PREFIX = "shared_";
    String DELIMITER = "_";

    String RELIABILITY_CACHE = "drools.reliability.cache";
    String RELIABILITY_CACHE_ALLOWED_PACKAGES = RELIABILITY_CACHE + ".allowedpackages";
    String RELIABILITY_CACHE_DIRECTORY = RELIABILITY_CACHE + ".dir";
    String RELIABILITY_CACHE_MODE = RELIABILITY_CACHE + ".mode";
    String RELIABILITY_CACHE_REMOTE_HOST = RELIABILITY_CACHE + ".remote.host";
    String RELIABILITY_CACHE_REMOTE_PORT = RELIABILITY_CACHE + ".remote.port";
    String RELIABILITY_CACHE_REMOTE_USER = RELIABILITY_CACHE + ".remote.user";
    String RELIABILITY_CACHE_REMOTE_PASS = RELIABILITY_CACHE + ".remote.pass";

    CacheManager getCacheManager();

    class Holder {
        private static final CacheManagerFactory INSTANCE = createInstance();

        static CacheManagerFactory createInstance() {
            CacheManagerFactory factory = KieService.load( CacheManagerFactory.class );
            if (factory == null) {
                throwExceptionForMissingRuntime();
                return null;
            }
            return factory;
        }
    }

    static CacheManagerFactory get() {
        return CacheManagerFactory.Holder.INSTANCE;
    }

    static <T> T throwExceptionForMissingRuntime() {
        throw new RuntimeException("Cannot find any persistence implementation");
    }
}

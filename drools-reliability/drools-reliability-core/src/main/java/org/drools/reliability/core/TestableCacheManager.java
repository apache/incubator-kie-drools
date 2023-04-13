package org.drools.reliability.core;

public interface TestableCacheManager extends CacheManager {

    void restart();

    void restartWithCleanUp();

    boolean isRemote();
}

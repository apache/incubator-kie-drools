package org.drools.reliability.core;

public interface TestableStorageManager extends StorageManager {

    void restart();

    void restartWithCleanUp();

    boolean isRemote();
}

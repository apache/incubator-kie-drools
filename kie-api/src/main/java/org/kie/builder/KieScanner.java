package org.kie.builder;

public interface KieScanner {
    void start(long pollingInterval);
    void stop();

    void scanNow();
}

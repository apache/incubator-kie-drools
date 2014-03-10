package org.kie.scanner.management;

public interface KieScannerMBean {

    public abstract String getScannerReleaseId();

    public abstract String getCurrentReleaseId();

    public abstract String getStatus();

    public abstract void scanNow();

    public abstract void start(long pollingInterval);

    public abstract void stop();
    
    public abstract void shutdown();

}
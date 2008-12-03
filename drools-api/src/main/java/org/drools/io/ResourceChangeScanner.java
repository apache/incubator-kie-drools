package org.drools.io;

import java.util.Properties;


public interface ResourceChangeScanner extends ResourceChangeMonitor {   
    
    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration();
    
    public ResourceChangeScannerConfiguration newResourceChangeScannerConfiguration(Properties properties);
    
    public void configure(ResourceChangeScannerConfiguration configuration);
    
    public void scan();

    public void start();

    public void stop();
    
    public void setInterval(int interval);
    
    public int getInterval();
        
}


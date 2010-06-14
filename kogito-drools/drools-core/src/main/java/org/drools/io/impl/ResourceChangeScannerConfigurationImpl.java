package org.drools.io.impl;

import java.util.Properties;

import org.drools.core.util.StringUtils;
import org.drools.io.ResourceChangeScannerConfiguration;

public class ResourceChangeScannerConfigurationImpl implements ResourceChangeScannerConfiguration {
    
    private int interval;
    
    public ResourceChangeScannerConfigurationImpl() {
        interval = 60;
    }
    
    public ResourceChangeScannerConfigurationImpl(Properties properties) {
        for( Object key : properties.keySet() ) {
            setProperty( (String) key, (String) properties.get( key ) );
        }
    }
    
    public void setProperty(String name,
                            String value) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return;
        } 
        
        if ( name.equals(   "drools.resource.scanner.interval" ) ) {
            setInterval( StringUtils.isEmpty( value ) ? 60 : Integer.parseInt( value ) );
        } 
    }

    public String getProperty(String name) {
        name = name.trim();
        if ( StringUtils.isEmpty( name ) ) {
            return null;
        } 
        
        if ( name.equals(   "drools.resource.scanner.interval" ) ) {
            return Integer.toString( this.interval );
        } 
        
        return null;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
   
}

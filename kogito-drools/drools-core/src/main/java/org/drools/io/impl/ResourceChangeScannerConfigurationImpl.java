/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

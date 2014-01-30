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

package org.drools.core.builder.conf.impl;

import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTableConfigurationImpl extends ResourceConfigurationImpl implements DecisionTableConfiguration {
    public static final String DROOLS_DT_TYPE = "drools.dt.type";
    public static final String DROOLS_DT_WORKSHEET = "drools.dt.worksheet";

    private final Logger logger = LoggerFactory.getLogger( DecisionTableConfigurationImpl.class ); 
    
    private DecisionTableInputType inputType = DecisionTableInputType.XLS;
    
    private String worksheetName;
    
    public DecisionTableConfigurationImpl() {
    }
    
    public void setInputType(DecisionTableInputType inputType) {
        this.inputType = inputType;
    }
    
    public DecisionTableInputType getInputType() {
        return this.inputType;
    }

    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public String getWorksheetName() {
        return this.worksheetName;
    }
    
    public Properties toProperties() {
        Properties prop = super.toProperties();
        prop.setProperty( DROOLS_DT_TYPE, inputType.toString() );
        if( worksheetName != null ) {
            prop.setProperty( DROOLS_DT_WORKSHEET, worksheetName );
        }
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        super.fromProperties(prop);
        inputType = DecisionTableInputType.valueOf( prop.getProperty( DROOLS_DT_TYPE, DecisionTableInputType.XLS.toString() ) );
        worksheetName = prop.getProperty( DROOLS_DT_WORKSHEET, null );
        return this;
    }
  
}

/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.impl;

import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.ResourceConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DecisionTableConfigurationImpl implements DecisionTableConfiguration {
    private final Logger logger = LoggerFactory.getLogger( DecisionTableConfigurationImpl.class );
    
    private DecisionTableInputType inputType;
    
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
        Properties prop = new Properties();
        prop.setProperty( "drools.dt.type", inputType.toString() );
        prop.setProperty( "drools.dt.worksheet", worksheetName );
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        inputType = DecisionTableInputType.valueOf( prop.getProperty( "drools.dt.type", "XLS" ) );
        worksheetName = prop.getProperty( "drools.dt.worksheet", null );
        return this;
    }
  
}

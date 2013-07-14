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
import org.kie.internal.builder.ScoreCardConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreCardConfigurationImpl implements ScoreCardConfiguration {
    public static final String DROOLS_SCARD_WORKSHEET = "drools.dt.worksheet";
    public static final String DROOLS_SCARD_USE_EXTERNAL_TYPES = "drools.sc.useExternalTypes";

    private final Logger logger = LoggerFactory.getLogger( ScoreCardConfigurationImpl.class );

    private String worksheetName;
    private boolean useExternalTypes = false;

    public ScoreCardConfigurationImpl() {
    }
    
    public void setWorksheetName(String worksheetName) {
        this.worksheetName = worksheetName;
    }

    public String getWorksheetName() {
        return this.worksheetName;
    }

    public void setUsingExternalTypes(boolean useExternalTypes) {
        this.useExternalTypes = useExternalTypes;
    }

    public boolean IsUsingExternalTypes() {
        return useExternalTypes;
    }

    public Properties toProperties() {
        Properties prop = new Properties();
        prop.setProperty( DROOLS_SCARD_WORKSHEET, worksheetName );
        prop.setProperty( DROOLS_SCARD_USE_EXTERNAL_TYPES, Boolean.toString(useExternalTypes));
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        worksheetName = prop.getProperty( DROOLS_SCARD_WORKSHEET, null );
        useExternalTypes = Boolean.getBoolean(prop.getProperty(DROOLS_SCARD_USE_EXTERNAL_TYPES, "false"));
        return this;
    }
  
}

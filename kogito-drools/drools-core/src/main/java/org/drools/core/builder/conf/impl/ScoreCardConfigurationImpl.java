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

package org.drools.core.builder.conf.impl;

import java.util.Properties;

import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScoreCardConfigurationImpl extends ResourceConfigurationImpl implements ScoreCardConfiguration {
    public static final String DROOLS_SCARD_WORKSHEET = "drools.dt.worksheet";
    public static final String DROOLS_SCARD_USE_EXTERNAL_TYPES = "drools.sc.useExternalTypes";
    public static final String DROOLS_SCARD_INPUT_TYPE = "drools.sc.inputType";

    private final Logger logger = LoggerFactory.getLogger( ScoreCardConfigurationImpl.class );

    private String worksheetName;
    private boolean useExternalTypes = false;
    private SCORECARD_INPUT_TYPE inputType;

    public ScoreCardConfigurationImpl() {
        inputType = SCORECARD_INPUT_TYPE.EXCEL;
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

    @Override
    public void setInputType(SCORECARD_INPUT_TYPE inputType) {
        this.inputType = inputType;
    }

    @Override
    public String getInputType() {
        return inputType.toString();
    }

    public Properties toProperties() {
        Properties prop = super.toProperties();
        prop.setProperty( DROOLS_SCARD_WORKSHEET, worksheetName );
        prop.setProperty( DROOLS_SCARD_USE_EXTERNAL_TYPES, Boolean.toString(useExternalTypes));
        prop.setProperty( DROOLS_SCARD_INPUT_TYPE, inputType.toString());
        return prop;
    }

    public ResourceConfiguration fromProperties(Properties prop) {
        super.fromProperties(prop);
        worksheetName = prop.getProperty( DROOLS_SCARD_WORKSHEET, null );
        useExternalTypes = Boolean.getBoolean(prop.getProperty(DROOLS_SCARD_USE_EXTERNAL_TYPES, "false"));
        String inputTypeStr = prop.getProperty( DROOLS_SCARD_INPUT_TYPE, SCORECARD_INPUT_TYPE.EXCEL.toString() );
        inputType = SCORECARD_INPUT_TYPE.valueOf(inputTypeStr);
        return this;
    }
  
}

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.RuleTemplateConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecisionTableConfigurationImpl extends ResourceConfigurationImpl implements DecisionTableConfiguration {
    public static final String DROOLS_DT_TYPE = "drools.dt.type";
    public static final String DROOLS_DT_WORKSHEET = "drools.dt.worksheet";

    private final Logger logger = LoggerFactory.getLogger( DecisionTableConfigurationImpl.class ); 
    
    private DecisionTableInputType inputType = DecisionTableInputType.XLS;
    
    private String worksheetName;

    private Set<RuleTemplateConfiguration> templates = new HashSet<RuleTemplateConfiguration>();

    private boolean trimCell = true;

    public DecisionTableConfigurationImpl() {
    }

    @Override
    public boolean isTrimCell() {
        return trimCell;
    }

    @Override
    public void setTrimCell( boolean trimCell ) {
        this.trimCell = trimCell;
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

    public void addRuleTemplateConfiguration(Resource template, int row, int col) {
        templates.add(new RuleTemplateInfo( template, row, col ));
    }

    public List<RuleTemplateConfiguration> getRuleTemplateConfigurations() {
        List<RuleTemplateConfiguration> confs = new ArrayList<>();
        confs.addAll( templates );
        return confs;
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

    public static class RuleTemplateInfo implements RuleTemplateConfiguration {
        private final Resource template;
        private final int row;
        private final int col;

        public RuleTemplateInfo( Resource template, int row, int col ) {
            this.template = template;
            this.row = row;
            this.col = col;
        }

        public Resource getTemplate() {
            return template;
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            RuleTemplateInfo that = ( RuleTemplateInfo ) o;

            if ( row != that.row ) return false;
            if ( col != that.col ) return false;
            return template != null ? template.equals( that.template ) : that.template == null;
        }

        @Override
        public int hashCode() {
            int result = template != null ? template.hashCode() : 0;
            result = 31 * result + row;
            result = 31 * result + col;
            return result;
        }
    }
}

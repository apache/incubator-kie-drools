/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.kie.builder.impl;

import java.util.List;
import java.util.Properties;

import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.RuleTemplateConfiguration;

public class DecisionTableConfigurationDelegate implements DecisionTableConfiguration {

    private final DecisionTableConfiguration delegate;
    private final String sheetName;

    public DecisionTableConfigurationDelegate( DecisionTableConfiguration delegate, String sheetName ) {
        this.delegate = delegate;
        this.sheetName = sheetName;
    }

    @Override
    public void setInputType( DecisionTableInputType inputType ) {
        delegate.setInputType( inputType );

    }

    @Override
    public DecisionTableInputType getInputType() {
        return delegate.getInputType();
    }

    @Override
    public void setWorksheetName( String name ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getWorksheetName() {
        return sheetName;
    }

    @Override
    public void addRuleTemplateConfiguration(Resource template, int row, int col ) {
        delegate.addRuleTemplateConfiguration( template, row, col );
    }

    @Override
    public List<RuleTemplateConfiguration> getRuleTemplateConfigurations() {
        return delegate.getRuleTemplateConfigurations();
    }

    @Override
    public boolean isTrimCell() {
        return delegate.isTrimCell();
    }

    @Override
    public void setTrimCell( boolean trimCell ) {
        delegate.setTrimCell( trimCell );
    }

    @Override
    public Properties toProperties() {
        return delegate.toProperties();
    }

    @Override
    public ResourceConfiguration fromProperties(Properties prop ) {
        return delegate.fromProperties( prop );
    }
}
/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.kie.services.impl.query;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.api.query.model.QueryDefinition;


public class SqlQueryDefinition implements QueryDefinition, Serializable {

    private static final long serialVersionUID = 1L;
    
    private String name;
    private String source;
    private String expression;
    
    private Target target = Target.CUSTOM;
    
    private Map<String, String> columns = new HashMap<String, String>();

    public SqlQueryDefinition(String name, String source) {
        this.name = name;
        this.source = source;
    }
    
    public SqlQueryDefinition(String name, String source, Target target) {
        this.name = name;
        this.source = source;
        this.target = target;
    }

    @Override
    public String getName() {
        
        return this.name;
    }

    @Override
    public void setName(String name) {
        
        this.name = name;
    }

    @Override
    public String getSource() {
        
        return this.source;
    }

    @Override
    public void setSource(String source) {
        
        this.source = source;
    }

    @Override
    public String getExpression() {
        
        return this.expression;
    }

    @Override
    public void setExpression(String expression) {
        
        this.expression = expression;
    }

    @Override
    public Target getTarget() {
        return this.target;
    }
    
    public void setTarget(Target target) {
        this.target = target;
    }

    @Override
    public String toString() {
        return "SqlQueryDefinition [name=" + name + ", source=" + source  + ", target=" + target + ", "+ 
                "{ expression=" + expression + "}]";
    }

    public Map<String, String> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, String> columns) {
        this.columns = columns;
    }

}

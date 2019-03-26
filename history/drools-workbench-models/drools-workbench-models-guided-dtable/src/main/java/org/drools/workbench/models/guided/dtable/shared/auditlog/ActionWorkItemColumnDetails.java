/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.auditlog;

import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemCol52;

/**
 * Details of an ActionWorkItem column, for when a Work Item is executed
 */
public class ActionWorkItemColumnDetails extends ColumnDetails {

    private String name;
    private Map<String, PortableParameterDefinition> parameters;

    public ActionWorkItemColumnDetails() {
    }

    public ActionWorkItemColumnDetails( final ActionWorkItemCol52 column ) {
        super( column );
        this.name = column.getWorkItemDefinition().getName();
        this.parameters = new HashMap<String, PortableParameterDefinition>();
        for ( String parameterName : column.getWorkItemDefinition().getParameterNames() ) {
            this.parameters.put( parameterName,
                                 column.getWorkItemDefinition().getParameter( parameterName ) );
        }
    }

    public String getName() {
        return name;
    }

    public Map<String, PortableParameterDefinition> getParameters() {
        return parameters;
    }

}

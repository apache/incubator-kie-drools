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

import org.drools.workbench.models.guided.dtable.shared.model.ActionWorkItemSetFieldCol52;

/**
 * Details of an ActionWorkItemSetField column
 */
public class ActionWorkItemSetFieldColumnDetails extends ColumnDetails {

    private String boundName;
    private String factField;
    private String workItemName;
    private String workItemResultParameterName;

    public ActionWorkItemSetFieldColumnDetails() {
    }

    public ActionWorkItemSetFieldColumnDetails( final ActionWorkItemSetFieldCol52 column ) {
        super( column );
        this.boundName = column.getBoundName();
        this.factField = column.getFactField();
        this.workItemName = column.getWorkItemName();
        this.workItemResultParameterName = column.getWorkItemResultParameterName();
    }

    public String getBoundName() {
        return boundName;
    }

    public String getFactField() {
        return factField;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public String getWorkItemResultParameterName() {
        return workItemResultParameterName;
    }

}

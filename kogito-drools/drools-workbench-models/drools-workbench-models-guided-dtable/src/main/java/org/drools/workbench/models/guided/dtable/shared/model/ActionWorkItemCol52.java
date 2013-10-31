/*
 * Copyright 2011 JBoss Inc
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
package org.drools.workbench.models.guided.dtable.shared.model;

import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;

import java.util.List;

/**
 * A column representing the execution of a Work Item.
 */
public class ActionWorkItemCol52 extends ActionCol52 {

    private static final long serialVersionUID = 540l;

    private PortableWorkDefinition workItemDefinition;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_WORKITEM_DEFINITION = "workItemDefinition";

    @Override
    public List<BaseColumnFieldDiff> diff(BaseColumn otherColumn) {
        if (otherColumn == null) return null;

        List<BaseColumnFieldDiff> result = super.diff(otherColumn);
        ActionWorkItemCol52 other = (ActionWorkItemCol52) otherColumn;

        // Field: default value.
        if ( !isEqualOrNull( this.getWorkItemDefinition(),
                other.getWorkItemDefinition() ) ) {
            result.add(new BaseColumnFieldDiffImpl(FIELD_WORKITEM_DEFINITION, this.getWorkItemDefinition(), other.getWorkItemDefinition()));
        }

        return result;
    }

    public PortableWorkDefinition getWorkItemDefinition() {
        return workItemDefinition;
    }

    public void setWorkItemDefinition( PortableWorkDefinition workItemDefinition ) {
        this.workItemDefinition = workItemDefinition;
    }

}

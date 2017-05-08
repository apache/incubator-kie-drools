/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.workbench.models.datamodel.workitems.PortableParameterDefinition;
import org.drools.workbench.models.datamodel.workitems.PortableWorkDefinition;

/**
 * A column representing the execution of a Work Item.
 */
public class ActionWorkItemCol52 extends ActionCol52 {

    private static final long serialVersionUID = 540l;

    private PortableWorkDefinition workItemDefinition;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_WORKITEM_DEFINITION_NAME = "workItemDefinitionName";

    public static final String FIELD_WORKITEM_DEFINITION_PARAMETER_NAME = "workItemDefinitionParameterName";

    public static final String FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE = "workItemDefinitionParameterValue";

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        ActionWorkItemCol52 other = (ActionWorkItemCol52) otherColumn;

        // Field: Work Item definition.
        final PortableWorkDefinition thisDefinition = this.getWorkItemDefinition();
        final PortableWorkDefinition otherDefinition = other.getWorkItemDefinition();

        //Determine diffs between "this" WorkItemDefinition and the "other" WorkItemDefinition
        //If both are null there are no changes; if however one is null and the other is not
        //then the WorkItemDefinition has effectively been added or deleted. Otherwise look
        //for differences between the two WorkItemDefinitions
        if ( thisDefinition == null && otherDefinition == null ) {
            return result;

        } else if ( thisDefinition != null && otherDefinition == null ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_NAME,
                                                     thisDefinition.getName(),
                                                     null ) );
            for ( PortableParameterDefinition parameter : thisDefinition.getParameters() ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_NAME,
                                                         parameter.getName(),
                                                         null ) );
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                         parameter.asString(),
                                                         null ) );
            }

        } else if ( thisDefinition == null && otherDefinition != null ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_NAME,
                                                     null,
                                                     otherDefinition.getName() ) );
            for ( PortableParameterDefinition parameter : otherDefinition.getParameters() ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_NAME,
                                                         null,
                                                         parameter.getName() ) );
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                         null,
                                                         parameter.asString() ) );
            }

        } else {
            if ( !thisDefinition.getName().equals( otherDefinition.getName() ) ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_NAME,
                                                         thisDefinition.getName(),
                                                         otherDefinition.getName() ) );
            }
            final List<String> thisDefinitionParameterNames = Arrays.asList( thisDefinition.getParameterNames() );
            final List<String> otherDefinitionParameterNames = Arrays.asList( otherDefinition.getParameterNames() );

            //Some Parameters have been deleted
            final List<String> parameterNamesDeleted = new ArrayList<String>( thisDefinitionParameterNames );
            parameterNamesDeleted.removeAll( otherDefinitionParameterNames );
            for ( String parameterName : parameterNamesDeleted ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_NAME,
                                                         thisDefinition.getParameter( parameterName ).getName(),
                                                         null ) );
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                         thisDefinition.getParameter( parameterName ).asString(),
                                                         null ) );
            }

            //Some Parameters have been added
            final List<String> parameterNamesAdded = new ArrayList<String>( otherDefinitionParameterNames );
            parameterNamesAdded.removeAll( thisDefinitionParameterNames );
            for ( String parameterName : parameterNamesAdded ) {
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_NAME,
                                                         null,
                                                         otherDefinition.getParameter( parameterName ).getName() ) );
                result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                         null,
                                                         otherDefinition.getParameter( parameterName ).asString() ) );
            }

            //Some Parameters have been updated
            final List<String> parameterNamesUpdated = new ArrayList<String>( thisDefinitionParameterNames );
            parameterNamesUpdated.retainAll( otherDefinitionParameterNames );
            for ( String parameterName : parameterNamesUpdated ) {
                boolean parameterNamesDiffer = false;
                if ( !isEqualOrNull( thisDefinition.getParameter( parameterName ).getName(),
                                     otherDefinition.getParameter( parameterName ).getName() ) ) {
                    result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_NAME,
                                                             thisDefinition.getParameter( parameterName ).getName(),
                                                             otherDefinition.getParameter( parameterName ).getName() ) );
                    parameterNamesDiffer = true;
                }
                if ( !isEqualOrNull( thisDefinition.getParameter( parameterName ).asString(),
                                     otherDefinition.getParameter( parameterName ).asString() ) ) {
                    if ( !parameterNamesDiffer ) {
                        result.add( new WorkItemColumnParameterValueDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                                              thisDefinition.getParameter( parameterName ).getName(),
                                                                              thisDefinition.getParameter( parameterName ).asString(),
                                                                              otherDefinition.getParameter( parameterName ).asString() ) );
                    } else {
                        result.add( new BaseColumnFieldDiffImpl( FIELD_WORKITEM_DEFINITION_PARAMETER_VALUE,
                                                                 thisDefinition.getParameter( parameterName ).asString(),
                                                                 otherDefinition.getParameter( parameterName ).asString() ) );
                    }
                }
            }
        }

        return result;
    }

    public PortableWorkDefinition getWorkItemDefinition() {
        return workItemDefinition;
    }

    public void setWorkItemDefinition( PortableWorkDefinition workItemDefinition ) {
        this.workItemDefinition = workItemDefinition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionWorkItemCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ActionWorkItemCol52 that = (ActionWorkItemCol52) o;

        return workItemDefinition != null ? workItemDefinition.equals(that.workItemDefinition) : that.workItemDefinition == null;
    }

    @Override
    public int hashCode() {
        return workItemDefinition != null ? workItemDefinition.hashCode() : 0;
    }
}

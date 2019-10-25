/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.List;

/**
 * An Action to set an existing Fact's field value with the value of a Work Item
 * Definition's result parameter
 */
public class ActionWorkItemSetFieldCol52 extends ActionSetFieldCol52 {

    private static final long serialVersionUID = 540l;

    /**
     * Available fields for this type of column.
     */
    public static final String FIELD_WORK_ITEM_NAME = "workItemName";

    public static final String FIELD_WORK_ITEM_RESULT_PARAM_NAME = "workItemResultParameterName";

    public static final String FIELD_PARAMETER_CLASSNAME = "parameterClassName";

    private String workItemName;

    private String workItemResultParameterName;

    private String parameterClassName;

    @Override
    public List<BaseColumnFieldDiff> diff( BaseColumn otherColumn ) {
        if ( otherColumn == null ) {
            return null;
        }

        List<BaseColumnFieldDiff> result = super.diff( otherColumn );
        ActionWorkItemSetFieldCol52 other = (ActionWorkItemSetFieldCol52) otherColumn;

        // Field: workItemName.
        if ( !isEqualOrNull( this.getWorkItemName(),
                             other.getWorkItemName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_WORK_ITEM_NAME, this.getWorkItemName(), other.getWorkItemName() ) );
        }

        // Field: workItemResultParameterName.
        if ( !isEqualOrNull( this.getWorkItemResultParameterName(),
                             other.getWorkItemResultParameterName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_WORK_ITEM_RESULT_PARAM_NAME, this.getWorkItemResultParameterName(), other.getWorkItemResultParameterName() ) );
        }

        // Field: parameterClassName.
        if ( !isEqualOrNull( this.getParameterClassName(),
                             other.getParameterClassName() ) ) {
            result.add( new BaseColumnFieldDiffImpl( FIELD_PARAMETER_CLASSNAME, this.getParameterClassName(), other.getParameterClassName() ) );
        }

        return result;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public void setWorkItemName( String workItemName ) {
        this.workItemName = workItemName;
    }

    public String getWorkItemResultParameterName() {
        return workItemResultParameterName;
    }

    public void setWorkItemResultParameterName( String workItemResultParameterName ) {
        this.workItemResultParameterName = workItemResultParameterName;
    }

    public String getParameterClassName() {
        return parameterClassName;
    }

    public void setParameterClassName( String parameterClassName ) {
        this.parameterClassName = parameterClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ActionWorkItemSetFieldCol52)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        ActionWorkItemSetFieldCol52 that = (ActionWorkItemSetFieldCol52) o;

        if (workItemName != null ? !workItemName.equals(that.workItemName) : that.workItemName != null) {
            return false;
        }
        if (workItemResultParameterName != null ? !workItemResultParameterName.equals(that.workItemResultParameterName) : that.workItemResultParameterName != null) {
            return false;
        }
        return parameterClassName != null ? parameterClassName.equals(that.parameterClassName) : that.parameterClassName == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result=~~result;
        result = 31 * result + (workItemName != null ? workItemName.hashCode() : 0);
        result=~~result;
        result = 31 * result + (workItemResultParameterName != null ? workItemResultParameterName.hashCode() : 0);
        result=~~result;
        result = 31 * result + (parameterClassName != null ? parameterClassName.hashCode() : 0);
        result=~~result;
        return result;
    }
}

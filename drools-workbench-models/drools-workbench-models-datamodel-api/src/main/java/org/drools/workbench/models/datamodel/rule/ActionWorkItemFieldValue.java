/*
 * Copyright 2012 JBoss Inc
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
package org.drools.workbench.models.datamodel.rule;

/**
 * Holds field and Work Item definition parameters for actions
 */
public class ActionWorkItemFieldValue extends ActionFieldValue {

    private static final long serialVersionUID = 540L;

    private String workItemName;
    private String workItemParameterName;
    private String workItemParameterClassName;

    public ActionWorkItemFieldValue() {
    }

    public ActionWorkItemFieldValue( String factField,
                                     String fieldType,
                                     String workItemName,
                                     String workItemParameterName,
                                     String workItemParameterClassName ) {
        super( factField,
               null,
               fieldType );
        this.workItemName = workItemName;
        this.workItemParameterName = workItemParameterName;
        this.workItemParameterClassName = workItemParameterClassName;
    }

    public String getWorkItemName() {
        return workItemName;
    }

    public String getWorkItemParameterName() {
        return workItemParameterName;
    }

    public String getWorkItemParameterClassName() {
        return workItemParameterClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ActionWorkItemFieldValue that = (ActionWorkItemFieldValue) o;

        if (workItemName != null ? !workItemName.equals(that.workItemName) : that.workItemName != null) return false;
        if (workItemParameterClassName != null ? !workItemParameterClassName.equals(that.workItemParameterClassName) : that.workItemParameterClassName != null)
            return false;
        if (workItemParameterName != null ? !workItemParameterName.equals(that.workItemParameterName) : that.workItemParameterName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (workItemName != null ? workItemName.hashCode() : 0);
        result = 31 * result + (workItemParameterName != null ? workItemParameterName.hashCode() : 0);
        result = 31 * result + (workItemParameterClassName != null ? workItemParameterClassName.hashCode() : 0);
        return result;
    }
}

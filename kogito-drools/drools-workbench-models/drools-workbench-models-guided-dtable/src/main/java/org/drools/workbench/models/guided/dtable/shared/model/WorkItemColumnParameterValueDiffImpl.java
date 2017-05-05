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

public class WorkItemColumnParameterValueDiffImpl extends BaseColumnFieldDiffImpl {

    private String parameterName;

    /**
     * Default no-arg constructor for errai marshalling.
     */
    public WorkItemColumnParameterValueDiffImpl() {
    }

    public WorkItemColumnParameterValueDiffImpl( String fieldName,
                                                 String parameterName,
                                                 Object oldValue,
                                                 Object newValue ) {
        super( fieldName,
               oldValue,
               newValue );
        this.parameterName = parameterName;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName( String parameterName ) {
        this.parameterName = parameterName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        WorkItemColumnParameterValueDiffImpl that = (WorkItemColumnParameterValueDiffImpl) o;

        return parameterName != null ? parameterName.equals(that.parameterName) : that.parameterName == null;
    }

    @Override
    public int hashCode() {
        return parameterName != null ? parameterName.hashCode() : 0;
    }
}
/*
 * Copyright 2011 JBoss Inc
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
package org.drools.guvnor.models.guided.dtable.shared.model;

/**
 * An Action to set an existing Fact's field value with the value of a Work Item
 * Definition's result parameter
 */
public class ActionWorkItemSetFieldCol52 extends ActionSetFieldCol52 {

    private static final long serialVersionUID = 540l;

    private String workItemName;

    private String workItemResultParameterName;

    private String parameterClassName;

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

}

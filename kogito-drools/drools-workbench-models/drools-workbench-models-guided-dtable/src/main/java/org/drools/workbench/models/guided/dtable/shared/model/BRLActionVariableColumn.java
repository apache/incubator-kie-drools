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

/**
 * A column representing a single BRL fragment variable
 */
public class BRLActionVariableColumn extends ActionCol52
        implements
        BRLVariableColumn {

    private static final long serialVersionUID = 540l;

    private String varName;
    private String fieldType;
    private String factType;
    private String factField;

    public BRLActionVariableColumn() {
    }

    public BRLActionVariableColumn( String varName,
                                    String fieldType ) {
        this.varName = varName;
        this.fieldType = fieldType;
    }

    public BRLActionVariableColumn( String varName,
                                    String fieldType,
                                    String factType,
                                    String factField ) {
        this.varName = varName;
        this.fieldType = fieldType;
        this.factType = factType;
        this.factField = factField;
    }

    public String getVarName() {
        return varName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFactType() {
        return factType;
    }

    public String getFactField() {
        return factField;
    }

}

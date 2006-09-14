package org.drools.lang.descr;

/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class PredicateDescr extends BaseDescr {
    /**
     * 
     */
    private static final long serialVersionUID = 320;
    private final String fieldName;
    private String       text;

    private final String declaration;
    private String[]     declarations;

    private String       classMethodName;

    public PredicateDescr(final String fieldName,
                          final String declaration) {
        this.fieldName = fieldName;
        this.declaration = declaration;
    }

    public PredicateDescr(final String fieldName,
                          final String declaration,
                          final String text) {
        this.fieldName = fieldName;
        this.declaration = declaration;
        this.text = text;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public String getText() {
        return this.text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public String getDeclaration() {
        return this.declaration;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }
}
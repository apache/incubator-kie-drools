/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

import org.kie.api.io.Resource;

import java.util.List;

public class PredicateDescr extends RestrictionDescr {
    private static final long serialVersionUID = 510l;
    private Object            content;

    private String[]          declarations;

    private String            classMethodName;

    private List<String>      parameters;

    public PredicateDescr() { }

    public PredicateDescr(final Object text) {
        this(null, text);
    }

    public PredicateDescr(final Resource resource, final Object text) {
        this.content = text;
        setResource(resource);
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(final Object text) {
        this.content = text;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "predicate '" + content + "'";
    }


    public void copyParameters( BaseDescr base ) {
        if ( base instanceof RelationalExprDescr ) {
            setParameters( ((RelationalExprDescr) base).getParameters() );
        }
    }
}

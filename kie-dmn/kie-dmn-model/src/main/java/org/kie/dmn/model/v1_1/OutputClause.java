/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;

public class OutputClause extends DMNElement {

    private UnaryTests outputValues;
    private LiteralExpression defaultOutputEntry;
    private String name;
    private QName typeRef;

    public UnaryTests getOutputValues() {
        return outputValues;
    }

    public void setOutputValues( final UnaryTests value ) {
        this.outputValues = value;
    }

    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    public void setDefaultOutputEntry( final LiteralExpression value ) {
        this.defaultOutputEntry = value;
    }

    public String getName() {
        return name;
    }

    public void setName( final String value ) {
        this.name = value;
    }

    public QName getTypeRef() {
        return typeRef;
    }

    public void setTypeRef( final QName value ) {
        this.typeRef = value;
    }

}

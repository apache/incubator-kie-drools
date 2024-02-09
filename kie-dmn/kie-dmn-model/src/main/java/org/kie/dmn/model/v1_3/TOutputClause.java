/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.model.v1_3;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;

public class TOutputClause extends TDMNElement implements OutputClause {

    protected UnaryTests outputValues;
    protected LiteralExpression defaultOutputEntry;
    protected String name;
    /**
     * align to internal model
     */
    protected QName typeRef;

    @Override
    public UnaryTests getOutputValues() {
        return outputValues;
    }

    @Override
    public void setOutputValues(UnaryTests value) {
        this.outputValues = value;
    }

    @Override
    public LiteralExpression getDefaultOutputEntry() {
        return defaultOutputEntry;
    }

    @Override
    public void setDefaultOutputEntry(LiteralExpression value) {
        this.defaultOutputEntry = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(QName value) {
        this.typeRef = value;
    }

}

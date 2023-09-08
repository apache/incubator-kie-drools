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
package org.kie.dmn.trisotech.model.v1_3;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_3.KieDMNModelInstrumentedBase;
import org.kie.dmn.trisotech.model.api.NamedExpression;

public class TNamedExpression extends KieDMNModelInstrumentedBase implements NamedExpression {

    private String name;

    private Expression expression;

    private QName typeRef;

    public TNamedExpression() {
    };

    public TNamedExpression(String name, Expression exp) {
        this(name, exp, null);
    }

    public TNamedExpression(String name, Expression exp, QName typeRef) {
        this.name = name;
        this.expression = exp;
        this.typeRef = typeRef;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(Expression expr) {
        this.expression = expr;
    }

    @Override
    public QName getTypeRef() {
        return typeRef;
    }

    @Override
    public void setTypeRef(QName ref) {
        this.typeRef = ref;

    }

}

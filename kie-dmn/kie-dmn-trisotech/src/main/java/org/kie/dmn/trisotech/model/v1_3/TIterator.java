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

import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_3.TExpression;
import org.kie.dmn.trisotech.model.api.Iterator;

public class TIterator extends TExpression implements Iterator {

    private String var;

    private Expression inExpr;

    private Expression returnExpr;

    private IteratorType iteratorType;

    @Override
    public String getVariable() {
        return var;
    }

    @Override
    public Expression getIn() {
        return inExpr;
    }

    @Override
    public Expression getReturn() {
        return returnExpr;
    }

    @Override
    public IteratorType getIteratorType() {
        return iteratorType;
    }

    @Override
    public void setVariable(String var) {
        this.var = var;

    }

    @Override
    public void setIn(Expression expr) {
        this.inExpr = expr;

    }

    @Override
    public void setReturn(Expression expr) {
        this.returnExpr = expr;

    }

    @Override
    public void setIteratorType(IteratorType type) {
        this.iteratorType = type;

    }

}

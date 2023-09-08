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
package org.drools.ruleunits.dsl.constraints;

import org.drools.model.Index;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;

public abstract class AbstractConstraint<L, R> implements Constraint<L> {

    protected final Variable<L> leftVariable;
    protected final String leftFieldName;
    protected final Function1<L, R> leftExtractor;
    protected final Index.ConstraintType constraintType;

    public AbstractConstraint(Variable<L> leftVariable, String leftFieldName, Function1<L, R> leftExtractor, Index.ConstraintType constraintType) {
        this.leftVariable = leftVariable;
        this.leftFieldName = leftFieldName;
        this.leftExtractor = leftExtractor;
        this.constraintType = constraintType;
    }
}
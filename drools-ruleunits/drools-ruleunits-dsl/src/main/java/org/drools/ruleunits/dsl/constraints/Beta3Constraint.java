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

import java.util.UUID;

import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function3;

public class Beta3Constraint<L, R1, R2, R3, V> extends AbstractConstraint<L, V> {

    private final Variable<R1> rightVariable1;
    private final Variable<R2> rightVariable2;
    private final Variable<R3> rightVariable3;
    private final Function3<R1, R2, R3, V> rightExtractor;

    public Beta3Constraint(Variable<L> leftVariable, String fieldName, Function1<L, V> leftExtractor, Index.ConstraintType constraintType, Variable<R1> rightVariable1, Variable<R2> rightVariable2, Variable<R3> rightVariable3, Function3<R1, R2, R3, V> rightExtractor) {
        super(leftVariable, fieldName, leftExtractor, constraintType);
        this.rightVariable1 = rightVariable1;
        this.rightVariable2 = rightVariable2;
        this.rightVariable3 = rightVariable3;
        this.rightExtractor = rightExtractor;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId = UUID.randomUUID().toString();
        patternDef.expr(exprId, rightVariable1, rightVariable2, rightVariable3, (l, r1, r2, r3) -> constraintType.asPredicate().test(leftExtractor.apply(l), rightExtractor.apply(r1, r2, r3)), null, null);
    }
}
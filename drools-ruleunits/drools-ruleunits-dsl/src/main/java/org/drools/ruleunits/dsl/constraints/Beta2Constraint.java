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

import org.drools.model.BetaIndex2;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.model.functions.Function2;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.betaIndexedBy;

public class Beta2Constraint<L, R1, R2, V> extends AbstractConstraint<L, V> {

    private final Variable<R1> rightVariable1;
    private final Variable<R2> rightVariable2;
    private final Function2<R1, R2, V> rightExtractor;

    public Beta2Constraint(Variable<L> leftVariable, String fieldName, Function1<L, V> leftExtractor, Index.ConstraintType constraintType, Variable<R1> rightVariable1, Variable<R2> rightVariable2, Function2<R1, R2, V> rightExtractor) {
        super(leftVariable, fieldName, leftExtractor, constraintType);
        this.rightVariable1 = rightVariable1;
        this.rightVariable2 = rightVariable2;
        this.rightExtractor = rightExtractor;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId;
        BetaIndex2 betaIndex = null;
        PatternDSL.ReactOn reactOn = null;
        if (leftFieldName != null) {
            // TODO the exprId may be not unique enough and may cause false node sharing
            exprId = "expr:" + leftVariable.getType().getCanonicalName() + ":" + leftFieldName + ":" + constraintType + ":" + rightVariable1.getType().getCanonicalName() + ":" + rightVariable2.getType().getCanonicalName();
            betaIndex = betaIndexedBy( (Class<V>) Object.class, constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), leftFieldName), leftExtractor, rightExtractor, Object.class );
            reactOn = PatternDSL.reactOn(leftFieldName);
        } else {
            exprId = UUID.randomUUID().toString();
        }
        patternDef.expr(exprId, rightVariable1, rightVariable2, (l, r1, r2) -> constraintType.asPredicate().test(leftExtractor.apply(l), rightExtractor.apply(r1, r2)), betaIndex, reactOn);
    }
}
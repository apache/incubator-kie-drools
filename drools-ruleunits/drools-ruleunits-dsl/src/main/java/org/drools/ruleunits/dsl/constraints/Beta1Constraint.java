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

import org.drools.model.BetaIndex;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.betaIndexedBy;

public class Beta1Constraint<L, R, V> extends AbstractConstraint<L, V> {

    private final Variable<R> rightVariable;
    private final Function1<R, V> rightExtractor;

    public Beta1Constraint(Variable<L> leftVariable, String fieldName, Function1<L, V> leftExtractor, Index.ConstraintType constraintType, Variable<R> rightVariable, Function1<R, V> rightExtractor) {
        super(leftVariable, fieldName, leftExtractor, constraintType);
        this.rightVariable = rightVariable;
        this.rightExtractor = rightExtractor;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId;
        BetaIndex betaIndex = null;
        PatternDSL.ReactOn reactOn = null;
        if (leftFieldName != null) {
            // TODO the exprId may be not unique enough and may cause false node sharing
            exprId = "expr:" + leftVariable.getType().getCanonicalName() + ":" + leftFieldName + ":" + constraintType + ":" + rightVariable.getType().getCanonicalName();
            betaIndex = betaIndexedBy( (Class<V>) Object.class, constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), leftFieldName), leftExtractor, rightExtractor );
            reactOn = PatternDSL.reactOn(leftFieldName);
        } else {
            exprId = UUID.randomUUID().toString();
        }
        patternDef.expr(exprId, rightVariable, (l, r) -> constraintType.asPredicate().test(leftExtractor.apply(l), rightExtractor.apply(r)), betaIndex, reactOn);
    }
}
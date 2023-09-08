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

import org.drools.model.AlphaIndex;
import org.drools.model.Index;
import org.drools.model.PatternDSL;
import org.drools.model.Variable;
import org.drools.model.functions.Function1;
import org.drools.ruleunits.dsl.util.ClassIntrospectionCache;

import static org.drools.model.PatternDSL.alphaIndexedBy;

public class AlphaConstraintWithRightValue<L, R> extends AbstractConstraint<L, R> {

    private final R rightValue;

    public AlphaConstraintWithRightValue(Variable<L> variable, String fieldName, Function1<L, R> extractor, Index.ConstraintType constraintType, R rightValue) {
        super(variable, fieldName, extractor, constraintType);
        this.rightValue = rightValue;
    }

    @Override
    public void addConstraintToPattern(PatternDSL.PatternDef<L> patternDef) {
        String exprId;
        AlphaIndex alphaIndex = null;
        PatternDSL.ReactOn reactOn = null;
        if (leftFieldName != null) {
            exprId = "expr:" + leftVariable.getType().getCanonicalName() + ":" + leftFieldName + ":" + constraintType + ":" + rightValue;
            alphaIndex = rightValue != null ?
                    alphaIndexedBy( (Class<R>) rightValue.getClass(), constraintType, ClassIntrospectionCache.getFieldIndex(leftVariable.getType(), leftFieldName), leftExtractor, rightValue ) :
                    null;
            reactOn = PatternDSL.reactOn(leftFieldName);
        } else {
            exprId = UUID.randomUUID().toString();
        }
        patternDef.expr(exprId, p -> constraintType.asPredicate().test(leftExtractor.apply(p), rightValue), alphaIndex, reactOn);
    }
}
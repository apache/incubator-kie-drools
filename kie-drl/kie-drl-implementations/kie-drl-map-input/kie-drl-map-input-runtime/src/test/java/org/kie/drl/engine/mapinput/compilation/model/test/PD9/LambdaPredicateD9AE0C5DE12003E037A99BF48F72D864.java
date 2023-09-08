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
package org.kie.drl.engine.mapinput.compilation.model.test.PD9;

@org.drools.compiler.kie.builder.MaterializedLambda()
public enum LambdaPredicateD9AE0C5DE12003E037A99BF48F72D864 implements org.drools.model.functions.Predicate1<org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication>, org.drools.model.functions.HashedExpression {

    INSTANCE;

    public static final String EXPRESSION_HASH = "674AFDF7428C12A1A693D10DDF8D6759";

    public String getExpressionHash() {
        return EXPRESSION_HASH;
    }

    @Override()
    public boolean test(org.kie.drl.engine.mapinput.compilation.model.test.LoanApplication _this) throws Exception {
        return _this.isApproved();
    }

    @Override()
    public org.drools.model.functions.PredicateInformation predicateInformation() {
        org.drools.model.functions.PredicateInformation info = new org.drools.model.functions.PredicateInformation("approved");
        info.addRuleNames("CollectApprovedApplication", "org/kie/kogito/legacy/LoanRules.drl");
        return info;
    }
}

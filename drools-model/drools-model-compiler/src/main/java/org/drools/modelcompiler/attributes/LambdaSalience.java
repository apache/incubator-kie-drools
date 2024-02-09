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
package org.drools.modelcompiler.attributes;

import org.drools.base.base.ValueResolver;
import org.drools.base.rule.accessor.Salience;
import org.drools.core.reteoo.Tuple;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.model.DynamicValueSupplier;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Match;

import static org.drools.modelcompiler.consequence.LambdaConsequence.declarationsToFacts;

public class LambdaSalience extends DynamicAttributeEvaluator<Integer> implements Salience {

    public LambdaSalience( DynamicValueSupplier<Integer> supplier ) {
        super(supplier);
    }

    @Override
    public int getValue(Match match, Rule rule, ValueResolver valueResolver) {
        Tuple tuple = ((InternalMatch)match).getTuple();
        Object[] facts = declarationsToFacts(valueResolver, tuple, getDeclarations(tuple), supplier.getVariables() );
        return supplier.supply( facts );
    }

    @Override
    public int getValue() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public boolean isDefault() {
        return false;
    }
}

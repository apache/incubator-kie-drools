/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.attributes;

import org.drools.core.WorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Enabled;
import org.drools.core.spi.Tuple;
import org.drools.model.DynamicValueSupplier;

import static org.drools.modelcompiler.consequence.LambdaConsequence.declarationsToFacts;

public class LambdaEnabled extends DynamicAttributeEvaluator<Boolean> implements Enabled {

    public LambdaEnabled( DynamicValueSupplier<Boolean> supplier ) {
        super( supplier );
    }

    @Override
    public boolean getValue( Tuple tuple, Declaration[] declrs, RuleImpl rule, WorkingMemory workingMemory ) {
        Object[] facts = declarationsToFacts( workingMemory, tuple, getDeclarations(tuple), supplier.getVariables() );
        return supplier.supply( facts );
    }
}

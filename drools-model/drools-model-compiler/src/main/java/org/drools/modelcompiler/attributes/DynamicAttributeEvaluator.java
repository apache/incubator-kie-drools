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

import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Tuple;
import org.drools.model.DynamicValueSupplier;

public class DynamicAttributeEvaluator<T> {
    protected final DynamicValueSupplier<T> supplier;

    public DynamicAttributeEvaluator( DynamicValueSupplier<T> supplier ) {
        this.supplier = supplier;
    }

    protected Declaration[] getDeclarations(Tuple tuple) {
        Declaration[] declarations = new Declaration[supplier.getVariables().length];
        Declaration[] allDeclarations = ((RuleTerminalNode) tuple.getTupleSink()).getAllDeclarations();
        for (int i = 0; i < supplier.getVariables().length; i++) {
            for (Declaration d : allDeclarations) {
                if (d.getIdentifier().equals(supplier.getVariables()[i].getName())) {
                    declarations[i] = d;
                    break;
                }
            }
        }
        return declarations;
    }
}

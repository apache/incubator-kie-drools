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

package org.drools.model.consequences;

import org.drools.model.ConditionalConsequence;
import org.drools.model.RuleItemBuilder;
import org.drools.model.consequences.ConsequenceBuilder.ValidBuilder;
import org.drools.model.view.ExprViewItem;

public class ConditionalConsequenceBuilder implements RuleItemBuilder<ConditionalConsequence> {

    private final ExprViewItem expr;

    private ValidBuilder thenBuilder;

    public ConditionalConsequenceBuilder( ExprViewItem expr ) {
        this.expr = expr;
    }

    public ConditionalConsequenceBuilder then(ValidBuilder thenBuilder) {
        this.thenBuilder = thenBuilder;
        return this;
    }

    @Override
    public ConditionalConsequence get() {
        return new ConditionalConsequenceImpl(expr, thenBuilder.get(), null);
    }
}

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

package org.drools.modelcompiler.builder.generator.operatorspec;

import org.drools.core.spi.Evaluator;
import org.drools.model.functions.Operator;

import static org.drools.core.common.InternalFactHandle.dummyFactHandleOf;

public class CustomOperatorWrapper implements Operator.SingleValue<Object, Object> {

    private final Evaluator evaluator;
    private final String name;

    public CustomOperatorWrapper( Evaluator evaluator, String name ) {
        this.evaluator = evaluator;
        this.name = name;
    }

    @Override
    public boolean eval( Object o1, Object o2 ) {
        return evaluator.evaluate(null, null, dummyFactHandleOf(o2), null, dummyFactHandleOf(o1));
    }

    @Override
    public String getOperatorName() {
        return name;
    }
}

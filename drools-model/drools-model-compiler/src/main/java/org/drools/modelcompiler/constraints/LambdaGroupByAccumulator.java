/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import org.drools.core.WorkingMemory;
import org.drools.core.base.accumulators.GroupByAccumulator;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.FunctionN;

public class LambdaGroupByAccumulator extends GroupByAccumulator {

    private final Declaration[] groupingDeclarations;
    private final FunctionN groupingFunction;

    public LambdaGroupByAccumulator( Accumulator innerAccumulator, Declaration[] groupingDeclarations, FunctionN groupingFunction ) {
        super(innerAccumulator);
        this.groupingDeclarations = groupingDeclarations;
        this.groupingFunction = groupingFunction;
    }

    @Override
    protected Object getKey( Tuple tuple, InternalFactHandle handle, WorkingMemory workingMemory ) {
        Object[] args = new Object[groupingDeclarations.length];
        for (int i = 0; i < groupingDeclarations.length; i++) {
            Declaration declaration = groupingDeclarations[i];
            Object object = tuple != null && declaration.getOffset() < tuple.size() ? tuple.getObject(declaration.getOffset()) : handle.getObject();
            args[i] = declaration.getValue( workingMemory.getInternalWorkingMemory(), object );
        }
        return groupingFunction.apply( args );
    }
}

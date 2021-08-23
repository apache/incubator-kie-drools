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

package org.drools.modelcompiler.constraints;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.Tuple;
import org.drools.model.Binding;

public class BindingEvaluator {
    private final Declaration[] declarations;
    protected final Binding binding;

    public BindingEvaluator( Declaration[] declarations, Binding binding ) {
        this.declarations = declarations;
        this.binding = binding;
    }

    public Object evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory, Declaration[] declarations, Declaration[] innerDeclarations ) {
        return evaluate( getArguments( handle, tuple, workingMemory, declarations, innerDeclarations ) );
    }

    public Object evaluate( Object... args ) {
        return binding.eval( args );
    }

    public Declaration[] getDeclarations() {
        return declarations;
    }

    private Object[] getArguments( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory, Declaration[] declarations, Declaration[] innerDeclarations ) {
        Object[] params = new Object[declarations.length + innerDeclarations.length];
        for (int i = 0; i < innerDeclarations.length; i++) {
            params[i] = getArgument( handle, workingMemory, innerDeclarations[i], tuple );
        }
        for (int i = 0; i < declarations.length; i++) {
            params[i+innerDeclarations.length] = getArgument( handle, workingMemory, declarations[i], tuple );
        }
        return params;
    }

    public static Object getArgument( InternalFactHandle handle, InternalWorkingMemory workingMemory, Declaration declaration, Tuple tuple ) {
        int tupleIndex = declaration.getTupleIndex();
        return declaration.getValue(workingMemory, tuple != null && tupleIndex < tuple.size() ? tuple.get(tupleIndex) : handle);
    }
}

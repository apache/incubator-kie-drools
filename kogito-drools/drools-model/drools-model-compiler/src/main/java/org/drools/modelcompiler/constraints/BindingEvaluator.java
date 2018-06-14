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
    private final Binding binding;

    public BindingEvaluator( Declaration[] declarations, Binding binding ) {
        this.declarations = declarations;
        this.binding = binding;
    }

    public Object evaluate( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) {
        return evaluate( getArguments( handle, tuple, workingMemory ) );
    }

    public Object evaluate( Object... args ) {
        return binding.eval( args );
    }

    protected Object[] getArguments( InternalFactHandle handle, Tuple tuple, InternalWorkingMemory workingMemory ) {
        Object[] params = new Object[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            params[i] = getArgument( handle, workingMemory, declarations[i], tuple );
        }
        return params;
    }

    private Object getArgument( InternalFactHandle handle, InternalWorkingMemory workingMemory, Declaration declaration, Tuple tuple ) {
        Object object = tuple != null && declaration.getPattern().getOffset() < tuple.size() ? tuple.getObject(declaration.getPattern().getOffset()) : handle.getObject();
        return declaration.getValue(workingMemory, object);
    }
}

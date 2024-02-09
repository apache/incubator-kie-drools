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
package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.model.Binding;
import org.kie.api.runtime.rule.FactHandle;

public class BindingEvaluator {
    private final Declaration[] declarations;
    protected final Binding binding;

    public BindingEvaluator( Declaration[] declarations, Binding binding ) {
        this.declarations = declarations;
        this.binding = binding;
    }

    public Object evaluate( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator, Declaration[] declarations, Declaration[] innerDeclarations ) {
        return evaluate( getArguments( handle, tuple, reteEvaluator, declarations, innerDeclarations ) );
    }

    public Object evaluate( Object... args ) {
        return binding.eval( args );
    }

    public Declaration[] getDeclarations() {
        return declarations;
    }

    private Object[] getArguments( FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator, Declaration[] declarations, Declaration[] innerDeclarations ) {
        Object[] params = new Object[declarations.length + innerDeclarations.length];
        for (int i = 0; i < innerDeclarations.length; i++) {
            params[i] = getArgument( handle, reteEvaluator, innerDeclarations[i], tuple );
        }
        for (int i = 0; i < declarations.length; i++) {
            params[i+innerDeclarations.length] = getArgument( handle, reteEvaluator, declarations[i], tuple );
        }
        return params;
    }

    public static Object getArgument(FactHandle handle, ValueResolver reteEvaluator, Declaration declaration, BaseTuple tuple) {
        int tupleIndex = declaration.getTupleIndex();
        return declaration.getValue(reteEvaluator, tuple != null && tupleIndex < tuple.size() ? tuple.get(tupleIndex) : handle);
    }
}

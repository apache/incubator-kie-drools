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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.Function1;

import java.util.Collections;
import java.util.Iterator;

public class LambdaDataProvider implements DataProvider {

    private final Function1 providerFunction;
    private Declaration declaration;

    public LambdaDataProvider( Declaration declaration, Function1 providerFunction ) {
        this.declaration = declaration;
        this.providerFunction = providerFunction;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[] { declaration };
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public Iterator getResults( Tuple tuple, InternalWorkingMemory wm, PropagationContext ctx, Object providerContext ) {
        Object obj = tuple.get( declaration ).getObject();
        Object result = providerFunction.apply( obj );
        if ( result instanceof Iterator ) {
            return (( Iterator ) result);
        }
        if ( result instanceof Iterable ) {
            return (( Iterable ) result).iterator();
        }
        return Collections.singletonList( result ).iterator();
    }

    @Override
    public DataProvider clone() {
        return this;
    }

    @Override
    public void replaceDeclaration( Declaration declaration, Declaration resolved ) {
        if (this.declaration.getIdentifier().equals( declaration.getIdentifier() )) {
            this.declaration = resolved;
        }
    }

    @Override
    public boolean isReactive() {
        return false;
    }
}

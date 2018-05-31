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

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.ReactiveObject;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.FunctionN;

public class LambdaDataProvider implements DataProvider {

    private final FunctionN providerFunction;
    private Declaration[] declarations;
    private final boolean reactive;

    public LambdaDataProvider( FunctionN providerFunction, boolean reactive, Declaration... declarations  ) {
        this.declarations = declarations;
        this.providerFunction = providerFunction;
        this.reactive = reactive;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return declarations;
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public Iterator getResults( Tuple tuple, InternalWorkingMemory wm, PropagationContext ctx, Object providerContext ) {
        Object result = getResult( tuple, wm );

        if (isReactive()) {
            if ( result instanceof ReactiveObject ) {
                (( ReactiveObject ) result).addLeftTuple( tuple );
            }
            if ( result instanceof Iterable ) {
                for (Object value : ( Iterable<?> ) result) {
                    if ( value instanceof ReactiveObject ) {
                        (( ReactiveObject ) value).addLeftTuple( tuple );
                    }
                }
            }
        }

        if ( result instanceof Object[] ) {
            return Arrays.asList( (Object[]) result ).iterator();
        }
        if ( result instanceof Iterator ) {
            return (( Iterator ) result);
        }
        if ( result instanceof Iterable ) {
            return (( Iterable ) result).iterator();
        }
        return Collections.singletonList( result ).iterator();
    }

    private Object getResult( Tuple tuple, InternalWorkingMemory wm ) {
        Object result;
        if (declarations.length == 0) {
            result = providerFunction.apply();
        } else if (declarations.length == 1) {
            result = getValueForDeclaration( tuple, wm, declarations[0] );
            if ( providerFunction != null ) {
                result = providerFunction.apply( result );
            }
        } else {
            Object[] args = new Object[declarations.length];
            for (int i = 0; i < declarations.length; i++) {
                args[i] = getValueForDeclaration( tuple, wm, declarations[i] );
            }
            result = providerFunction.apply( args );
        }
        return result;
    }

    private Object getValueForDeclaration( Tuple tuple, InternalWorkingMemory wm, Declaration declaration ) {
        return declaration.getExtractor().isGlobal() ?
                declaration.getExtractor().getValue( wm, declaration.getIdentifier() ) :
                declaration.getValue( wm, tuple.get( declaration ).getObject() );
    }

    @Override
    public DataProvider clone() {
        Declaration[] clonedDecls = new Declaration[declarations.length];
        for (int i = 0; i < declarations.length; i++) {
            clonedDecls[i] = declarations[i].clone();
        }
        return new LambdaDataProvider( providerFunction, reactive, clonedDecls );
    }

    @Override
    public void replaceDeclaration( Declaration declaration, Declaration resolved ) {
        for (int i = 0; i < declarations.length; i++) {
            if ( this.declarations[i].getIdentifier().equals( declaration.getIdentifier() ) ) {
                this.declarations[i] = resolved;
            }
        }
    }

    @Override
    public boolean isReactive() {
        return reactive;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        LambdaDataProvider that = ( LambdaDataProvider ) o;
        return reactive == that.reactive &&
                Objects.equals( providerFunction, that.providerFunction ) &&
                Arrays.equals( declarations, that.declarations );
    }

    @Override
    public int hashCode() {
        return Objects.hash( providerFunction, Arrays.hashCode( declarations ), reactive );
    }
}

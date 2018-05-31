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

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.DataProvider;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.model.functions.Function0;

public class SupplierDataProvider implements DataProvider {

    private final Function0 supplierFunction;
    private final boolean reactive;


    public SupplierDataProvider(Function0 providerFunction) {
        this.supplierFunction = providerFunction;
        this.reactive = false;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[] {  };
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public Iterator getResults( Tuple tuple, InternalWorkingMemory wm, PropagationContext ctx, Object providerContext ) {
        Object result = null;
        if (supplierFunction != null) {
            result = supplierFunction.apply();
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

    @Override
    public DataProvider clone() {
        return new SupplierDataProvider(supplierFunction );
    }

    @Override
    public void replaceDeclaration( Declaration declaration, Declaration resolved ) {

    }

    @Override
    public boolean isReactive() {
        return reactive;
    }
}

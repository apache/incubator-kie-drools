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

package org.drools.mvel.dataproviders;

import java.util.Iterator;

import org.drools.base.base.ValueResolver;
import org.drools.base.phreak.ReactiveObject;
import org.drools.base.reteoo.BaseTuple;
import org.drools.mvel.expr.MVELCompilationUnit;

public class ReactiveMVELDataProvider extends MVELDataProvider {

    public ReactiveMVELDataProvider() { }

    public ReactiveMVELDataProvider( MVELCompilationUnit unit, String id) {
        super(unit, id);
    }

    @Override
    public boolean isReactive() {
        return true;
    }

    @Override
    public Iterator getResults( final BaseTuple tuple,
                                final ValueResolver valueResolver,
                                final Object executionContext ) {
        Object result = evaluate( tuple, valueResolver );
        if (result instanceof ReactiveObject) {
            ( (ReactiveObject) result ).addTuple(tuple);
        }
        return asIterator( result );
    }
}

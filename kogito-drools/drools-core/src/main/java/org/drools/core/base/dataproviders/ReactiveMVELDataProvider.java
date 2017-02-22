/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base.dataproviders;

import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.phreak.ReactiveObject;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;

import java.util.Iterator;

public class ReactiveMVELDataProvider extends MVELDataProvider {

    public ReactiveMVELDataProvider() { }

    public ReactiveMVELDataProvider(MVELCompilationUnit unit, String id) {
        super(unit, id);
    }

    @Override
    public boolean isReactive() {
        return true;
    }

    @Override
    public Iterator getResults( final Tuple tuple,
                                final InternalWorkingMemory wm,
                                final PropagationContext ctx,
                                final Object executionContext ) {
        Object result = evaluate( tuple, wm );
        if (result instanceof ReactiveObject) {
            ( (ReactiveObject) result ).addLeftTuple( tuple );
        }
        return asIterator( result );
    }
}

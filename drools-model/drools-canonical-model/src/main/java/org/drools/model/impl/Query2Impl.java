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

package org.drools.model.impl;

import org.drools.model.Argument;
import org.drools.model.Query2;
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

public class Query2Impl<A, B> extends QueryImpl implements Query2<A, B> {
    private final Variable<A> var1;
    private final Variable<B> var2;

    public Query2Impl( String pkg, String name, View view, Variable<A> var1, Variable<B> var2 ) {
        super( pkg, name, view );
        this.var1 = var1;
        this.var2 = var2;
    }

    @Override
    public QueryCallViewItem call( Argument<A> aVar, Argument<B> bVar ) {
        return new QueryCallViewItemImpl( this, aVar, bVar );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { var1, var2 };
    }
}

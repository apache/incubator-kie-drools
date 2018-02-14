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
import org.drools.model.Query3Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

import static org.drools.model.FlowDSL.declarationOf;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query3DefImpl<A, B, C> extends QueryDefImpl implements Query3Def<A, B, C> {
    private final Variable<A> arg1;
    private final Variable<B> arg2;
    private final Variable<C> arg3;

    public Query3DefImpl(ViewBuilder viewBuilder, String name, Class<A> type1, Class<B> type2, Class<C> type3 ) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, type2, type3);
    }

    public Query3DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<A> type1, Class<B> type2, Class<C> type3  ) {
        super( viewBuilder, pkg, name );
        this.arg1 = declarationOf( type1 );
        this.arg2 = declarationOf( type2 );
        this.arg3 = declarationOf( type3 );
    }

    public Query3DefImpl(ViewBuilder viewBuilder, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name ) {
        this(viewBuilder, DEFAULT_PACKAGE, name, type1, arg1name, type2, arg2name, type3, arg3name);
    }

    public Query3DefImpl(ViewBuilder viewBuilder, String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name, Class<C> type3, String arg3name  ) {
        super( viewBuilder, pkg, name );
        this.arg1 = declarationOf( type1, arg1name);
        this.arg2 = declarationOf( type2, arg2name );
        this.arg3 = declarationOf( type3, arg3name);
    }

    @Override
    public QueryCallViewItem call( boolean open, Argument<A> aVar, Argument<B> bVar, Argument<C> cVar ) {
        return new QueryCallViewItemImpl( this, open, aVar, bVar, cVar );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] {arg1, arg2, arg3};
    }

    public Variable<A> getArg1() {
        return arg1;
    }

    public Variable<B> getArg2() {
        return arg2;
    }

    @Override
    public Variable<C> getArg3() {
        return arg3;
    }
}

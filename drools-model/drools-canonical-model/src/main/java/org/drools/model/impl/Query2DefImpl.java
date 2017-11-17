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
import org.drools.model.Query2Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.type;
import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query2DefImpl<A, B> extends QueryDefImpl implements Query2Def<A, B> {
    private final Variable<A> arg1;
    private final Variable<B> arg2;

    public Query2DefImpl( String name, Class<A> type1, Class<B> type2 ) {
        this(DEFAULT_PACKAGE, name, type1, type2);
    }

    public Query2DefImpl( String pkg, String name, Class<A> type1, Class<B> type2 ) {
        super( pkg, name );
        this.arg1 = declarationOf( type(type1) );
        this.arg2 = declarationOf( type(type2) );
    }

    public Query2DefImpl( String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        this(DEFAULT_PACKAGE, name, type1, arg1name, type2, arg2name);
    }

    public Query2DefImpl( String pkg, String name, Class<A> type1, String arg1name, Class<B> type2, String arg2name ) {
        super( pkg, name );
        this.arg1 = declarationOf( type(type1) , arg1name);
        this.arg2 = declarationOf( type(type2) , arg2name);
    }

    @Override
    public QueryCallViewItem call( boolean open, Argument<A> aVar, Argument<B> bVar ) {
        return new QueryCallViewItemImpl( this, open, aVar, bVar );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] {arg1, arg2};
    }

    public Variable<A> getArg1() {
        return arg1;
    }

    public Variable<B> getArg2() {
        return arg2;
    }
}

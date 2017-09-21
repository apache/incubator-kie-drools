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

import org.drools.model.Query0;
import org.drools.model.Query1;
import org.drools.model.Query2;
import org.drools.model.Variable;
import org.drools.model.view.AbstractExprViewItem;
import org.drools.model.view.ViewItemBuilder;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;
import static org.drools.model.impl.ViewBuilder.viewItems2Patterns;

public class QueryBuider {

    public static abstract class AbstractQueryBuilder {
        protected final String pkg;
        protected final String name;

        public AbstractQueryBuilder(String name) {
            this(DEFAULT_PACKAGE, name);
        }

        public AbstractQueryBuilder(String pkg, String name) {
            this.pkg = pkg;
            this.name = name;
        }
    }

    private static ViewItemBuilder[] asQueryExpresssion( ViewItemBuilder[] viewItemBuilders ) {
        for (ViewItemBuilder item : viewItemBuilders) {
            if (item instanceof AbstractExprViewItem ) {
                ( (AbstractExprViewItem) item ).setQueryExpression( true );
            }
        }
        return viewItemBuilders;
    }

    public static class _0<A> extends AbstractQueryBuilder {
        public _0( String name) {
            super(name);
        }

        public _0( String pkg, String name, Variable<A> var1 ) {
            super(name, pkg);
        }

        public Query0 view(ViewItemBuilder... viewItemBuilders ) {
            return new Query0Impl( pkg, name, viewItems2Patterns( asQueryExpresssion( viewItemBuilders ) ) );
        }
    }

    public static class _1<A> extends AbstractQueryBuilder {
        private final Variable<A> var1;

        public _1( String name, Variable<A> var1 ) {
            super(name);
            this.var1 = var1;
        }

        public _1( String pkg, String name, Variable<A> var1 ) {
            super(name, pkg);
            this.var1 = var1;
        }

        public Query1<A> view( ViewItemBuilder... viewItemBuilders ) {
            return new Query1Impl<>( pkg, name, viewItems2Patterns( asQueryExpresssion( viewItemBuilders ) ), var1 );
        }
    }

    public static class _2<A, B> extends AbstractQueryBuilder {
        private final Variable<A> var1;
        private final Variable<B> var2;

        public _2( String name, Variable<A> var1, Variable<B> var2 ) {
            super(name);
            this.var1 = var1;
            this.var2 = var2;
        }

        public _2( String pkg, String name, Variable<A> var1, Variable<B> var2 ) {
            super(name, pkg);
            this.var1 = var1;
            this.var2 = var2;
        }

        public Query2<A, B> view( ViewItemBuilder... viewItemBuilders ) {
            return new Query2Impl<>( pkg, name, viewItems2Patterns( asQueryExpresssion( viewItemBuilders ) ), var1, var2 );
        }
    }
}

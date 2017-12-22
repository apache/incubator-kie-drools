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

import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.model.view.AbstractExprViewItem;
import org.drools.model.view.ViewItemBuilder;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;
import static org.drools.model.impl.ViewBuilder.viewItems2Patterns;

public abstract class QueryDefImpl implements QueryDef {

    private final String pkg;
    private final String name;

    public QueryDefImpl( String name ) {
        this(DEFAULT_PACKAGE, name);
    }

    public QueryDefImpl( String pkg, String name ) {
        this.pkg = pkg;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPackage() {
        return pkg;
    }

    @Override
    public Query build( ViewItemBuilder... viewItemBuilders ) {
        return new QueryImpl( this, viewItems2Patterns( asQueryExpresssion( viewItemBuilders ) ) );
    }

    private static ViewItemBuilder[] asQueryExpresssion( ViewItemBuilder[] viewItemBuilders ) {
        for (ViewItemBuilder item : viewItemBuilders) {
            if (item instanceof AbstractExprViewItem ) {
                ( (AbstractExprViewItem) item ).setQueryExpression( true );
            }
        }
        return viewItemBuilders;
    }
}

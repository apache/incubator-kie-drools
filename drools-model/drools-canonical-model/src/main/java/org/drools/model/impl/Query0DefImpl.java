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

import org.drools.model.Query0Def;
import org.drools.model.Variable;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

import static org.drools.model.impl.RuleBuilder.DEFAULT_PACKAGE;

public class Query0DefImpl extends QueryDefImpl implements Query0Def {

    public Query0DefImpl( ViewBuilder viewBuilder, String name ) {
        this(viewBuilder, DEFAULT_PACKAGE, name);
    }

    public Query0DefImpl( ViewBuilder viewBuilder, String pkg, String name ) {
        super( viewBuilder, pkg, name );
    }

    @Override
    public QueryCallViewItem call(boolean open) {
        return new QueryCallViewItemImpl( this, open );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { };
    }
}

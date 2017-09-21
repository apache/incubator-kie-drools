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
import org.drools.model.Variable;
import org.drools.model.View;
import org.drools.model.view.QueryCallViewItem;
import org.drools.model.view.QueryCallViewItemImpl;

public class Query0Impl extends QueryImpl implements Query0 {

    public Query0Impl(String pkg, String name, View view) {
        super( pkg, name, view );
    }

    @Override
    public QueryCallViewItem call() {
        return new QueryCallViewItemImpl( this );
    }

    @Override
    public Variable<?>[] getArguments() {
        return new Variable<?>[] { };
    }
}

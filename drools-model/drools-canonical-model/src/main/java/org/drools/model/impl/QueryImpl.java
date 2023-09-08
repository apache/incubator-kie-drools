/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.impl;

import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.model.Variable;
import org.drools.model.View;

public class QueryImpl implements Query, ModelComponent {

    private final QueryDef queryDef;
    private final View view;

    public QueryImpl( QueryDef queryDef, View view ) {
        this.queryDef = queryDef;
        this.view = view;
    }

    @Override
    public View getView() {
        return view;
    }

    @Override
    public String getName() {
        return queryDef.getName();
    }

    @Override
    public String getPackage() {
        return queryDef.getPackage();
    }

    @Override
    public Variable<?>[] getArguments() {
        return queryDef.getArguments();
    }

    @Override
    public boolean isEqualTo( ModelComponent o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        QueryImpl query = ( QueryImpl ) o;

        return ModelComponent.areEqualInModel( view, query.view ) && ModelComponent.areEqualInModel( queryDef, query.queryDef );
    }
}

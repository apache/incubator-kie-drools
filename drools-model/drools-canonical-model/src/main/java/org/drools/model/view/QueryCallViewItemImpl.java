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
package org.drools.model.view;

import org.drools.model.Argument;
import org.drools.model.QueryDef;
import org.drools.model.Variable;

public class QueryCallViewItemImpl implements QueryCallViewItem {

    private final QueryDef query;
    private final boolean open;
    private final Argument<?>[] arguments;

    public QueryCallViewItemImpl( QueryDef query, boolean open, Argument<?>... arguments ) {
        this.query = query;
        this.open = open;
        this.arguments = arguments;
    }

    @Override
    public QueryDef getQuery() {
        return query;
    }

    @Override
    public Argument<?>[] getArguments() {
        return arguments;
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public Variable getFirstVariable() {
        return null;
    }

    @Override
    public Variable<?>[] getVariables() {
        throw new UnsupportedOperationException();
    }
}

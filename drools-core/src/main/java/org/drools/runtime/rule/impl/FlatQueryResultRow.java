/**
 * Copyright 2010 JBoss Inc
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

package org.drools.runtime.rule.impl;

import java.util.List;
import java.util.Map;

import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResultsRow;

public class FlatQueryResultRow
    implements
    QueryResultsRow {
    Map<String, Integer>     identifiers;
    private List             result;
    private List<FactHandle> factHandles;

    public FlatQueryResultRow(Map<String, Integer> identifiers,
                              List result,
                              List<FactHandle> factHandles) {
        this.identifiers = identifiers;
        this.result = result;
        this.factHandles = factHandles;
    }

    public Object get(String identifier) {
        return this.result.get( identifiers.get( identifier ) );
    }

    public FactHandle getFactHandle(String identifier) {
        return this.factHandles.get( identifiers.get( identifier ) );
    }

}

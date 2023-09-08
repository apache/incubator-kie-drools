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
package org.kie.api.runtime.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Contains the results of a query. The identifiers is a map of the declarations for the query, only patterns or fields that are bound can
 * be accessed in the QueryResultsRow. This class can be marshalled using the drools-drools-pipeline module in combination with the BatchExecutionHelper.
 * See the BatchExecutionHelper for more details.
 * </p>
 */
public interface QueryResults extends Iterable<QueryResultsRow> {
    String[] getIdentifiers();

    Iterator<QueryResultsRow> iterator();

    int size();

    default List<Map<String, Object>> toList() {
        String[] columns = getIdentifiers();
        List<Map<String, Object>> results = new ArrayList<>(size());
        for (QueryResultsRow row : this) {
            Map<String, Object> map = new HashMap<>();
            for (String col : columns) {
                map.put(col, row.get( col ));
            }
            results.add(map);
        }
        return results;
    }

    default <T> List<T> toList(String identifier) {
        List<Object> results = new ArrayList<>(size());
        for (QueryResultsRow row : this) {
            results.add(row.get( identifier ));
        }
        return (List<T>) results;
    }
}

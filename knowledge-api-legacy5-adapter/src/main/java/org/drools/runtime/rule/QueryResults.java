/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.runtime.rule;

import java.util.Iterator;

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
}

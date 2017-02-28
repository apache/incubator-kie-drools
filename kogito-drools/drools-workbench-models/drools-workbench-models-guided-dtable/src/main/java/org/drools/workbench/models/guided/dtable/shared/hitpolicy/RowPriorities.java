/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.hitpolicy;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * RowPriorities keeps track of what the salience values for the final dtable will end up being.
 */
class RowPriorities {

    private final Map<RowNumber, Salience> map = new TreeMap<>();

    Set<RowNumber> getRowNumbers() {
        return map.keySet();
    }

    Salience getSalience( final RowNumber rowNumber ) {
        return map.get( rowNumber );
    }

    public void put( final RowNumber rowNumber,
                     final Salience salience ) {
        map.put( rowNumber,
                 salience );
    }
}

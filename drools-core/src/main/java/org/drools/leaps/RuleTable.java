package org.drools.leaps;

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

import java.util.Comparator;

import org.drools.leaps.util.Table;

/**
 * Implementation of a container to store data elements used throughout the
 * leaps. Stores rule handles
 * 
 * @author Alexander Bagerman
 * 
 */
class RuleTable extends Table {
    /**
     * 
     */
    private static final long serialVersionUID = -1855260276968132243L;

    public RuleTable(final Comparator ruleConflictResolver) {
        super( ruleConflictResolver );
    }
}
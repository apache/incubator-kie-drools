package org.drools.leaps.conflict;

/*
 * Copyright 2006 Alexander Bagerman
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

import org.drools.leaps.RuleHandle;

/**
 * <code>RuleConflictResolver</code> that uses the rule complexity.
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.leaps.conflict.AbstractConflictResolver
 * @see org.drools.leaps.ConflictResolver
 * @see org.drools.spi.ConflictResolver
 */
class RuleComplexityConflictResolver
    implements
    Comparator {

    /** Singleton instance. */
    private static final RuleComplexityConflictResolver INSTANCE = new RuleComplexityConflictResolver();

    /**
     * Retrieve the singleton instance.
     * 
     * @return The singleton instance.
     */
    public static Comparator getInstance() {
        return INSTANCE;
    }

    /**
     * Construct.
     */
    private RuleComplexityConflictResolver() {
        // intentionally left blank
    }

    /**
     * @see LeapsRuleConflictResolver
     */
    public int compare(Object o1,
                       Object o2) {
        return (-1) * AbstractConflictResolver.compare( (((RuleHandle) o1).getRuleComplexity()),
                                                        (((RuleHandle) o2).getRuleComplexity()) );
    };
}

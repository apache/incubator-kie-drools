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

import org.drools.leaps.ConflictResolver;

/**
 * A blueprint for conflict resolers
 * 
 * @author Alexander Bagerman
 * 
 */

abstract class AbstractConflictResolver
    implements
    ConflictResolver {
    // need for comparator
    static int compare(int i1,
                       int i2) {
        return i1 - i2;
    }

    static int compare(long l1,
                       long l2) {
        return (int) (l1 - l2);
    }

    public abstract Comparator getFactConflictResolver();

    public abstract Comparator getRuleConflictResolver();
}

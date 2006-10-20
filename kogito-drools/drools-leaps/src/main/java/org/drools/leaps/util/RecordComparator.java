package org.drools.leaps.util;

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

import java.io.Serializable;
import java.util.Comparator;

/**
 * this class wraps object comparator to sort records holding given objects in
 * a list
 * 
 * @author Alexander Bagerman
 * 
 */

class RecordComparator
    implements
    Comparator,
    Serializable {
    private Comparator objectComparator;

    RecordComparator(final Comparator objectComparator) {
        this.objectComparator = objectComparator;
    }

    public int compare(final Object record1,
                       final Object record2) {
        return this.objectComparator.compare( ((TableRecord) record1).object,
                                              ((TableRecord) record2).object );
    }

}

/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;

public interface LeftTupleSets {
    LeftTuple getInsertFirst();
    LeftTuple getDeleteFirst();
    LeftTuple getUpdateFirst();

    void resetAll();

    boolean addInsert(LeftTuple leftTuple);
    boolean addDelete(LeftTuple leftTuple);
    boolean addUpdate(LeftTuple leftTuple);

    void removeInsert(LeftTuple leftTuple);
    void removeDelete(LeftTuple leftTuple);
    void removeUpdate(LeftTuple leftTuple);

    void addAllInserts(LeftTupleSets tupleSets);
    void addAllDeletes(LeftTupleSets tupleSets);
    void addAllUpdates(LeftTupleSets tupleSets);

    void addAll(LeftTupleSets source);

    LeftTupleSets takeAll();

    boolean isEmpty();

    String toStringSizes();

    LeftTuple getNormalizedDeleteFirst();
    boolean addNormalizedDelete(LeftTuple leftTuple);
}

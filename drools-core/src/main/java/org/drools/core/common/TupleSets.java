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
 *
 */

package org.drools.core.common;

import org.drools.core.spi.Tuple;

public interface TupleSets<T extends Tuple> {
    T getInsertFirst();
    T getDeleteFirst();
    T getUpdateFirst();

    void resetAll();

    boolean addInsert(T leftTuple);
    boolean addDelete(T leftTuple);
    boolean addUpdate(T leftTuple);

    void removeInsert(T leftTuple);
    void removeDelete(T leftTuple);
    void removeUpdate(T leftTuple);

    void addAllInserts(TupleSets<T> tupleSets);
    void addAllDeletes(TupleSets<T> tupleSets);
    void addAllUpdates(TupleSets<T> tupleSets);

    void addAll(TupleSets<T> source);

    TupleSets<T> takeAll();

    boolean isEmpty();

    String toStringSizes();

    T getNormalizedDeleteFirst();
    boolean addNormalizedDelete(T leftTuple);
}

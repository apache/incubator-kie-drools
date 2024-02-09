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
package org.drools.core.common;

import org.drools.core.reteoo.TupleImpl;

public interface TupleSets {
    TupleImpl getInsertFirst();
    TupleImpl getDeleteFirst();
    TupleImpl getUpdateFirst();

    int getInsertSize();

    void resetAll();

    /**
     * clear also ensures all contained LeftTuples are cleared
     * reset does not touch any contained tuples
     */
    void clear();

    boolean addInsert(TupleImpl leftTuple);
    boolean addDelete(TupleImpl leftTuple);
    boolean addUpdate(TupleImpl leftTuple);

    void removeInsert(TupleImpl leftTuple);
    void removeDelete(TupleImpl leftTuple);
    void removeUpdate(TupleImpl leftTuple);

    void addAll(TupleSets source);

    void addTo(TupleSets target);

    TupleSets takeAll();

    boolean isEmpty();

    String toStringSizes();

    TupleImpl getNormalizedDeleteFirst();
    boolean addNormalizedDelete(TupleImpl leftTuple);
}

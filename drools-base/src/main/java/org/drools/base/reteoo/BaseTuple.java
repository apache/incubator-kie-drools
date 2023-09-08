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
package org.drools.base.reteoo;

import org.drools.base.rule.Declaration;
import org.kie.api.runtime.rule.FactHandle;

public interface BaseTuple {
    /**
     * Returns the <code>FactHandle</code> for the given <code>Declaration</code>, which in turn
     * specifcy the <code>Pattern</code> that they depend on.
     *
     * @param declaration
     *      The <code>Declaration</code> which specifies the <code>Pattern</code>
     * @return
     *      The <code>FactHandle</code>
     */
    FactHandle get(Declaration declaration);

    /**
     * Returns the <code>FactHandle</code> for the given pattern index. If the pattern is empty
     * It returns null.
     *
     * @param pattern
     *      The index of the pattern from which the <code>FactHandleImpl</code> is to be returned
     * @return
     *      The <code>FactHandle</code>
     */
    FactHandle get(int pattern);

    FactHandle getFactHandle();

    Object getObject(int pattern);

    Object getObject(Declaration declaration);

    /**
     * Returns the size of this tuple in number of elements (patterns)
     */
    int size();

    Object[] toObjects();

    Object[] toObjects(boolean reverse);

    /**
     * Returns the fact handles in reverse order
     */
    FactHandle[] toFactHandles();

    BaseTuple getParent();

    /**
     * Returns the tuple at the given index
     * @param index
     * @return
     */
    BaseTuple getTuple(int index);

    int getIndex();

    Object getContextObject();

    BaseTuple skipEmptyHandles();
}

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
package org.drools.core.time.impl;

import org.drools.base.time.JobHandle;
import org.drools.core.util.LinkedListNode;

public abstract class AbstractJobHandle<T extends AbstractJobHandle> implements JobHandle,
                                                   LinkedListNode<T> {

    private T previous;
    private T next;

    @Override
    public T getPrevious() {
        return previous;
    }

    @Override
    public void setPrevious(T previous) {
        this.previous = previous;
    }

    @Override
    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    @Override
    public void setNext(T next) {
        this.next = next;
    }

    @Override
    public T getNext() {
        return next;
    }
}

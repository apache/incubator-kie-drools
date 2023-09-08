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
package org.drools.mvel.compiler;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MockPersistentSet extends AbstractSet implements Set
{

    private Set set;

    private boolean exception;

    public MockPersistentSet()
    {
        exception = true;
        set = new HashSet();
    }

    public MockPersistentSet(boolean exception)
    {
        this.exception = exception;
        set = new HashSet();
    }

    public int size()
    {
        return set.size();
    }

    public Iterator iterator()
    {
        return set.iterator();
    }

    public boolean addAll(Collection c)
    {
        if (exception)
            throw new MockPersistentSetException("error message like PersistentSet");
        return set.addAll(c);
    }

}

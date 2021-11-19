/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import java.util.Collection;

public abstract class AbstractImmutableCollection
        implements
        Collection {

    public boolean add(Object o) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean addAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public void clear() {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean remove(Object o) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean removeAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }

    public boolean retainAll(Collection c) {
        throw new UnsupportedOperationException( "This is an immmutable Collection" );
    }
}
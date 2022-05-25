/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.core.datatype.impl.coverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CloneHelperTest {

    public static class CollectionHolder<T> {

        private final Collection<T> collection;

        public Collection<T> getCollection() {
            return collection;
        }

        public CollectionHolder(Collection<T> collection) {
            this.collection = collection;
        }
    }

    public static class CloneableCollectionHolder<T> extends CollectionHolder<T> implements Cloneable {
        public CloneableCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        @Override
        public Object clone() {
            return new CloneableCollectionHolder<>(new ArrayList<>(super.getCollection()));
        }
    }

    public static class DumbCloneableCollectionHolder<T> extends CollectionHolder<T> implements Cloneable {
        public DumbCloneableCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        @Override
        public Object clone() {
            throw new UnsupportedOperationException();
        }
    }

    public static class DumbCopyCollectionHolder<T> extends CollectionHolder<T> {
        public DumbCopyCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        public DumbCopyCollectionHolder(CollectionHolder<T> collection) {
            super(new ArrayList<>(collection.getCollection()));
            throw new RuntimeException();
        }
    }

    public static class CopyCollectionHolder<T> extends CollectionHolder<T> {
        public CopyCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        public CopyCollectionHolder(CopyCollectionHolder<T> collection) {
            super(new ArrayList<>(collection.getCollection()));
        }
    }

    @Test
    void testCloneable() {
        CollectionHolder<Integer> toClone = new CloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = CloneHelper.clone(toClone);
        assertNotSame(toClone.getCollection(), cloned.getCollection());
        assertEquals(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testCopyConstructor() {
        CollectionHolder<Integer> toClone = new CopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = CloneHelper.clone(toClone);
        assertNotSame(toClone.getCollection(), cloned.getCollection());
        assertEquals(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testDefault() {
        CollectionHolder<Integer> toClone = new CollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = CloneHelper.clone(toClone);
        assertSame(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testCloneableError() {
        CollectionHolder<Integer> toClone = new DumbCloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        assertThrows(IllegalStateException.class, () -> CloneHelper.clone(toClone));
    }

    @Test
    void testCopyError() {
        CollectionHolder<Integer> toClone = new DumbCopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        assertThrows(IllegalStateException.class, () -> CloneHelper.clone(toClone));
    }
}

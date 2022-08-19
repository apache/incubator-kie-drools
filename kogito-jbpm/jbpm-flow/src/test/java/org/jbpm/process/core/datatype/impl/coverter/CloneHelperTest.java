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
import java.util.Objects;
import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CloneHelperTest {

    private static class CollectionHolder<T> {

        private final Collection<T> collection;

        public Collection<T> getCollection() {
            return collection;
        }

        public CollectionHolder(Collection<T> collection) {
            this.collection = collection;
        }
    }

    private static class CloneableCollectionHolder<T> extends CollectionHolder<T> implements Cloneable {
        public CloneableCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        @Override
        public Object clone() {
            return new CloneableCollectionHolder<>(new ArrayList<>(super.getCollection()));
        }
    }

    private static class DumbCloneableCollectionHolder<T> extends CollectionHolder<T> implements Cloneable {
        public DumbCloneableCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        @Override
        public Object clone() {
            throw new UnsupportedOperationException();
        }
    }

    private static class LierCloneableCollectionHolder<T> extends CollectionHolder<T> implements Cloneable {
        public LierCloneableCollectionHolder(Collection<T> collection) {
            super(collection);
        }
    }

    private static class DumbCopyCollectionHolder<T> extends CollectionHolder<T> {
        public DumbCopyCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        public DumbCopyCollectionHolder(CollectionHolder<T> collection) {
            super(new ArrayList<>(collection.getCollection()));
            throw new RuntimeException();
        }
    }

    private static class CopyCollectionHolder<T> extends CollectionHolder<T> {
        public CopyCollectionHolder(Collection<T> collection) {
            super(collection);
        }

        public CopyCollectionHolder(CopyCollectionHolder<T> collection) {
            super(new ArrayList<>(collection.getCollection()));
        }
    }

    private static class CustomCloneable {
        private final String name;

        public CustomCloneable(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (!(obj instanceof CustomCloneable))
                return false;
            CustomCloneable other = (CustomCloneable) obj;
            return Objects.equals(name, other.name);
        }
    }

    @Test
    void testCloneable() {
        CollectionHolder<Integer> toClone = new CloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = (CollectionHolder<Integer>) TypeConverterRegistry.get().forTypeCloner(toClone.getClass()).apply(toClone);
        assertNotSame(toClone.getCollection(), cloned.getCollection());
        assertEquals(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testCopyConstructor() {
        CollectionHolder<Integer> toClone = new CopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = (CollectionHolder<Integer>) TypeConverterRegistry.get().forTypeCloner(toClone.getClass()).apply(toClone);
        assertNotSame(toClone.getCollection(), cloned.getCollection());
        assertEquals(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testDefault() {
        CollectionHolder<Integer> toClone = new CollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        CollectionHolder<Integer> cloned = (CollectionHolder<Integer>) TypeConverterRegistry.get().forTypeCloner(toClone.getClass()).apply(toClone);
        assertSame(toClone.getCollection(), cloned.getCollection());
    }

    @Test
    void testCloneableError() {
        CollectionHolder<Integer> toClone = new DumbCloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        UnaryOperator<Object> cloner = TypeConverterRegistry.get().forTypeCloner(toClone.getClass());
        assertThrows(IllegalStateException.class, () -> cloner.apply(toClone));
    }

    @Test
    void testCopyError() {
        CollectionHolder<Integer> toClone = new DumbCopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        UnaryOperator<Object> cloner = TypeConverterRegistry.get().forTypeCloner(toClone.getClass());
        assertThrows(IllegalStateException.class, () -> cloner.apply(toClone));
    }

    @Test
    void NoCloneableError() {
        CollectionHolder<Integer> toClone = new LierCloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4));
        assertSame(toClone, TypeConverterRegistry.get().forTypeCloner(toClone.getClass()).apply(toClone));
    }

    @Test
    void testCloneRegister() {
        TypeConverterRegistry.get().registerCloner(CustomCloneable.class, o -> new CustomCloneable(o.getName() + "_" + o.getName()));
        CustomCloneable toClone = new CustomCloneable("Javierito");
        assertEquals(new CustomCloneable("Javierito_Javierito"), TypeConverterRegistry.get().forTypeCloner(toClone.getClass()).apply(toClone));
    }
}

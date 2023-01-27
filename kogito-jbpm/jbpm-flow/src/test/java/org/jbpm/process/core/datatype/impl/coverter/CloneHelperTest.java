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

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CloneHelperTest {

    private static class CollectionHolder<T> {

        private final Collection<T> collection;

        public Collection<T> getCollection() {
            return collection;
        }

        public CollectionHolder(Collection<T> collection) {
            this.collection = collection;
        }

        @Override
        public int hashCode() {
            return Objects.hash(collection);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CollectionHolder other = (CollectionHolder) obj;
            return Objects.equals(collection, other.collection);
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

    @BeforeAll
    static void init() {
        CloneHelperRegister.get().registerCloner(CustomCloneable.class, o -> new CustomCloneable(o.getName() + "_" + o.getName()));
    }

    @Test
    void testCloneable() {
        assertCloned(new CloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testCopyConstructor() {
        assertCloned(new CopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testDefault() {
        assertNotCloned(new CollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testCloneableError() {
        assertCloneError(new DumbCloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testCopyError() {
        assertCloneError(new DumbCopyCollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testNoCloneable() {
        assertNotCloned(new LierCloneableCollectionHolder<>(Arrays.asList(1, 2, 3, 4)));
    }

    @Test
    void testCloneRegister() {
        assertThat(CloneHelper.get().clone(new CustomCloneable("Javierito"))).isEqualTo(new CustomCloneable("Javierito_Javierito"));
    }

    @Test
    void testCloneIntPrimitive() {
        assertNotCloned(1);
    }

    @Test
    void testCloneBoolPrimitive() {
        assertNotCloned(true);
    }

    @Test
    void testCloneStringPrimitive() {
        assertCloned("pepe");
    }

    @Test
    void testCloneNull() {
        Object clode = CloneHelper.get().clone(null);
        assertThat(clode).isNull();
    }

    @Test
    void testListClone() {
        assertCloned(new ArrayList<>(Arrays.asList(1, 2, 3)));
    }

    @Test
    void testObjectNodeClone() {
        assertThat(CloneHelper.get().getCloner(ObjectNode.class)).isSameAs(CloneHelper.get().getCloner(JsonNode.class));
        assertCloned(ObjectMapperFactory.get().createObjectNode().put("name", "Javierito"));
    }

    private void assertNotCloned(Object toClone) {
        assertThat(CloneHelper.get().clone(toClone)).isSameAs(toClone);
    }

    private void assertCloned(Object toClone) {
        Object cloned = CloneHelper.get().clone(toClone);
        assertThat(cloned).isEqualTo(toClone).isNotSameAs(toClone);
    }

    private <T> void assertCloneError(T toClone) {
        UnaryOperator<T> cloner = (UnaryOperator<T>) CloneHelper.get().getCloner(toClone.getClass());
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> cloner.apply(toClone));
    }
}

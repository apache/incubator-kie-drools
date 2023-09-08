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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ClassAwareObjectStoreTest {

    private final ClassAwareObjectStore underTest;

    @Test
    public void iterateObjectsReturnsObjectsOfAllTypes() throws Exception {
        String aStringValue = "a string";
        BigDecimal bigDecimalValue = new BigDecimal("1");

        insertObjectWithFactHandle(aStringValue);
        insertObjectWithFactHandle(bigDecimalValue);

        Collection<Object> result = collect(underTest.iterateObjects());
        assertThat(result).hasSize(2);
    }

    @Test
    public void iterateByClassFiltersByClass() {
        SimpleClass object = new SimpleClass();

        insertObjectWithFactHandle("some string");
        insertObjectWithFactHandle(object);
        Collection<Object> results = collect(underTest.iterateObjects(SimpleClass.class));

        assertThat(results).hasSize(1);
        assertThat(results).contains(object);
    }

    @Test
    public void queryBySuperTypeFindsSubType() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
        assertThat(result).hasAtLeastOneElementOfType(SuperClass.class);
    }

    @Test
    public void queryBySubtypeDoesNotReturnSuperType() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    /**
     * Should have identical results to {@link #queryBySuperTypeFindsSubType()}
     */
    @Test
    public void queryBySubTypeDoesNotPreventInsertionsBeingPropogatedToSuperTypeQueries() throws Exception {
        insertObjectWithFactHandle(new SuperClass());
        collect(underTest.iterateObjects(SubClass.class));
        insertObjectWithFactHandle(new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
        assertThat(result).hasAtLeastOneElementOfType(SuperClass.class);
    }

    @Test
    public void queryBySuperTypeCanFindSubTypeWhenNoSuperTypeInstancesAreInStore() throws Exception {
        insertObjectWithFactHandle(new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    @Test
    public void isOkayToReinsertSameTypeThenQuery() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SubClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result)).hasSize(2);
    }

    @Test
    public void onceSuperClassIsSetUpForReadingItCanBecomeSetUpForWritingWithoutGettingDuplicateQueryReturns() throws Exception {
        assertThat(collect(underTest.iterateObjects(SuperClass.class)).isEmpty()).isTrue();

        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result)).hasSize(2);
    }

    @Test
    public void clearRemovesInsertedObjects() throws Exception {
        insertObjectWithFactHandle(new SimpleClass());
        assertThat(collect(underTest.iterateObjects())).hasSize(1);

        underTest.clear();

        assertThat(collect(underTest.iterateObjects())).hasSize(0);
    }

    @Test
    public void canIterateOverObjectsUsingCustomFilter() throws Exception {
        insertObjectWithFactHandle(new SuperClass());
        insertObjectWithFactHandle(new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class::isInstance));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    @Test
    public void iteratingOverFactHandlesHasSameNumberOfResultsAsIteratingOverObjects() throws Exception {
        insertObjectWithFactHandle(new SuperClass());
        insertObjectWithFactHandle(new SubClass());

        assertThat(collect(underTest.iterateFactHandles(SubClass.class))).hasSize(1);
        assertThat(collect(underTest.iterateFactHandles(SuperClass.class))).hasSize(2);
    }


    private void insertObjectWithFactHandle(Object objectToInsert) {
        underTest.addHandle(handleFor(objectToInsert), objectToInsert);
    }

    public ClassAwareObjectStore getUnderTest() {
        return underTest;
    }

    private static <T> Collection<T> collect(Iterator<T> objects) {
        List<T> result = new ArrayList<T>();
        while (objects.hasNext()) {
            result.add(objects.next());
        }
        return result;
    }

    public ClassAwareObjectStoreTest(boolean isEqualityBehaviour) {
        underTest = new ClassAwareObjectStore(isEqualityBehaviour, new ReentrantLock());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> ruleBaseConfigurations() {
        List<Object[]> configurations = new ArrayList<Object[]>(2);
        configurations.add(new Object[]{true});
        configurations.add(new Object[]{false});
        return configurations;
    }

    private static final AtomicInteger factCounter = new AtomicInteger(0);

    private InternalFactHandle handleFor(Object object) {
        return new DefaultFactHandle(factCounter.getAndIncrement(), object);
    }

    private static class SimpleClass {
    }

    private static class SuperClass {
    }

    private static class SubClass extends SuperClass {
    }
}
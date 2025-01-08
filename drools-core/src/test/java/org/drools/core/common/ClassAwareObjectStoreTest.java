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
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassAwareObjectStoreTest {


    @ParameterizedTest
	@MethodSource("parameters")
    public void iterateObjectsReturnsObjectsOfAllTypes(ClassAwareObjectStore underTest) throws Exception {
        String aStringValue = "a string";
        BigDecimal bigDecimalValue = new BigDecimal("1");

        underTest.addHandle(handleFor(aStringValue), aStringValue);
        underTest.addHandle(handleFor(bigDecimalValue), bigDecimalValue);

        Collection<Object> result = collect(underTest.iterateObjects());
        assertThat(result).hasSize(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void iterateByClassFiltersByClass(ClassAwareObjectStore underTest) {
        SimpleClass object = new SimpleClass();

        underTest.addHandle(handleFor("some string"), "some string");
        underTest.addHandle(handleFor(object), object);
        Collection<Object> results = collect(underTest.iterateObjects(SimpleClass.class));

        assertThat(results).hasSize(1);
        assertThat(results).contains(object);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void queryBySuperTypeFindsSubType(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SubClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SuperClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
        assertThat(result).hasAtLeastOneElementOfType(SuperClass.class);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void queryBySubtypeDoesNotReturnSuperType(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SubClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SuperClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    /**
     * Should have identical results to {@link #queryBySuperTypeFindsSubType()}
     */
    @ParameterizedTest
	@MethodSource("parameters")
    public void queryBySubTypeDoesNotPreventInsertionsBeingPropogatedToSuperTypeQueries(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SuperClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
        collect(underTest.iterateObjects(SubClass.class));
		Object objectToInsert1 = new SubClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
        assertThat(result).hasAtLeastOneElementOfType(SuperClass.class);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void queryBySuperTypeCanFindSubTypeWhenNoSuperTypeInstancesAreInStore(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SubClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void isOkayToReinsertSameTypeThenQuery(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SubClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SubClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result)).hasSize(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void onceSuperClassIsSetUpForReadingItCanBecomeSetUpForWritingWithoutGettingDuplicateQueryReturns(ClassAwareObjectStore underTest) throws Exception {
        assertThat(collect(underTest.iterateObjects(SuperClass.class)).isEmpty()).isTrue();
		Object objectToInsert = new SubClass();

        underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SuperClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result)).hasSize(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void clearRemovesInsertedObjects(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SimpleClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
        assertThat(collect(underTest.iterateObjects())).hasSize(1);

        underTest.clear();

        assertThat(collect(underTest.iterateObjects())).hasSize(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void canIterateOverObjectsUsingCustomFilter(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SuperClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SubClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class::isInstance));

        assertThat(result).hasSize(1);
        assertThat(result).hasAtLeastOneElementOfType(SubClass.class);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void iteratingOverFactHandlesHasSameNumberOfResultsAsIteratingOverObjects(ClassAwareObjectStore underTest) throws Exception {
        Object objectToInsert = new SuperClass();
		underTest.addHandle(handleFor(objectToInsert), objectToInsert);
		Object objectToInsert1 = new SubClass();
        underTest.addHandle(handleFor(objectToInsert1), objectToInsert1);

        assertThat(collect(underTest.iterateFactHandles(SubClass.class))).hasSize(1);
        assertThat(collect(underTest.iterateFactHandles(SuperClass.class))).hasSize(2);
    }


    private static <T> Collection<T> collect(Iterator<T> objects) {
        List<T> result = new ArrayList<T>();
        while (objects.hasNext()) {
            result.add(objects.next());
        }
        return result;
    }
    
    public static Stream<ClassAwareObjectStore> parameters() {
    	return Stream.of(new ClassAwareObjectStore(true, new ReentrantLock()), 
    			new ClassAwareObjectStore(false, new ReentrantLock()));
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
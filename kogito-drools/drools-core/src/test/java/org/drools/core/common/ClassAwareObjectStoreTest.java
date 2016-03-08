package org.drools.core.common;

import org.drools.core.ObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        assertThat(result.size(), is(equalTo(2)));
    }

    @Test
    public void iterateByClassFiltersByClass() {
        SimpleClass object = new SimpleClass();

        insertObjectWithFactHandle("some string");
        insertObjectWithFactHandle(object);
        Collection<Object> results = collect(underTest.iterateObjects(SimpleClass.class));

        assertThat(results.size(), is(equalTo(1)));
        assertThat(results, hasItem(object));
    }

    @Test
    public void queryBySuperTypeFindsSubType() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result.size(), is(equalTo(2)));
        assertThat(result, hasItem(isA(SubClass.class)));
        assertThat(result, hasItem(isA(SuperClass.class)));
    }

    @Test
    public void queryBySubtypeDoesNotReturnSuperType() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class));

        assertThat(result.size(), is(equalTo(1)));
        assertThat(result, hasItem(isA(SubClass.class)));
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

        assertThat(result.size(), is(equalTo(2)));
        assertThat(result, hasItem(isA(SubClass.class)));
        assertThat(result, hasItem(isA(SuperClass.class)));
    }

    @Test
    public void queryBySuperTypeCanFindSubTypeWhenNoSuperTypeInstancesAreInStore() throws Exception {
        insertObjectWithFactHandle(new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result.size(), is(equalTo(1)));
        assertThat(result, hasItem(isA(SubClass.class)));
    }

    @Test
    public void isOkayToReinsertSameTypeThenQuery() throws Exception {
        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SubClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result.size(), is(equalTo(2)));
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result).size(), is(equalTo(2)));
    }

    @Test
    public void onceSuperClassIsSetUpForReadingItCanBecomeSetUpForWritingWithoutGettingDuplicateQueryReturns() throws Exception {
        assertTrue(collect(underTest.iterateObjects(SuperClass.class)).isEmpty());

        insertObjectWithFactHandle(new SubClass());
        insertObjectWithFactHandle(new SuperClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result.size(), is(equalTo(2)));
        // Check there's no duplication of results
        assertThat(new HashSet<Object>(result).size(), is(equalTo(2)));
    }

    @Test
    public void clearRemovesInsertedObjects() throws Exception {
        insertObjectWithFactHandle(new SimpleClass());
        assertThat(collect(underTest.iterateObjects()).size(), is(equalTo(1)));

        underTest.clear();

        assertThat(collect(underTest.iterateObjects()).size(), is(equalTo(0)));
    }

    @Test
    public void canIterateOverObjectsUsingCustomFilter() throws Exception {
        insertObjectWithFactHandle(new SuperClass());
        insertObjectWithFactHandle(new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(new ObjectFilter() {
            @Override
            public boolean accept(Object o) {
                return SubClass.class.isInstance(o);
            }
        }));

        assertThat(result.size(), is(equalTo(1)));
        assertThat(result, hasItem(isA(SubClass.class)));
    }

    @Test
    public void iteratingOverFactHandlesHasSameNumberOfResultsAsIteratingOverObjects() throws Exception {
        insertObjectWithFactHandle(new SuperClass());
        insertObjectWithFactHandle(new SubClass());

        assertThat(collect(underTest.iterateFactHandles(SubClass.class)).size(), is(equalTo(1)));
        assertThat(collect(underTest.iterateFactHandles(SuperClass.class)).size(), is(equalTo(2)));
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

    public ClassAwareObjectStoreTest(RuleBaseConfiguration ruleBaseConfiguration) {
        underTest = new ClassAwareObjectStore(ruleBaseConfiguration, new ReentrantLock());
    }

    @Parameterized.Parameters
    public static Collection<Object[]> ruleBaseConfigurations() {
        List<Object[]> configurations = new ArrayList<Object[]>(2);
        configurations.add(new Object[]{new RuleBaseConfiguration() {{
            setAssertBehaviour(AssertBehaviour.EQUALITY);
        }}});
        configurations.add(new Object[]{new RuleBaseConfiguration() {{
            setAssertBehaviour(AssertBehaviour.IDENTITY);
        }}});
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
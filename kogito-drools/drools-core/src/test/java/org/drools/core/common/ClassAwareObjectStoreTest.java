package org.drools.core.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import org.drools.core.ObjectFilter;
import org.drools.core.RuleBaseConfiguration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.*;

public class ClassAwareObjectStoreTest {

    @ClassAwareObjectStoreParameterizedTest
    public void iterateObjectsReturnsObjectsOfAllTypes(final ClassAwareObjectStore underTest) throws Exception {
        String aStringValue = "a string";
        BigDecimal bigDecimalValue = new BigDecimal("1");

        insertObjectWithFactHandle(underTest, aStringValue);
        insertObjectWithFactHandle(underTest, bigDecimalValue);

        Collection<Object> result = collect(underTest.iterateObjects());
        assertThat(result).hasSize(2);
    }

    @ClassAwareObjectStoreParameterizedTest
    public void iterateByClassFiltersByClass(final ClassAwareObjectStore underTest) {
        SimpleClass object = new SimpleClass();

        insertObjectWithFactHandle(underTest, "some string");
        insertObjectWithFactHandle(underTest, object);
        Collection<Object> results = collect(underTest.iterateObjects(SimpleClass.class));

        assertThat(results).hasSize(1);
        assertThat(results).contains(object);
    }

    @ClassAwareObjectStoreParameterizedTest
    public void queryBySuperTypeFindsSubType(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SubClass());
        insertObjectWithFactHandle(underTest, new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SubClass.class));
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SuperClass.class));
    }

    @ClassAwareObjectStoreParameterizedTest
    public void queryBySubtypeDoesNotReturnSuperType(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SubClass());
        insertObjectWithFactHandle(underTest, new SuperClass());

        Collection<Object> result = collect(underTest.iterateObjects(SubClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SubClass.class));
    }

    /**
     * Should have identical results to {@link #queryBySuperTypeFindsSubType(ClassAwareObjectStore)} )}
     */
    @ClassAwareObjectStoreParameterizedTest
    public void queryBySubTypeDoesNotPreventInsertionsBeingPropogatedToSuperTypeQueries(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SuperClass());
        collect(underTest.iterateObjects(SubClass.class));
        insertObjectWithFactHandle(underTest, new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SubClass.class));
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SuperClass.class));
    }

    @ClassAwareObjectStoreParameterizedTest
    public void queryBySuperTypeCanFindSubTypeWhenNoSuperTypeInstancesAreInStore(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(1);
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SubClass.class));
    }

    @ClassAwareObjectStoreParameterizedTest
    public void isOkayToReinsertSameTypeThenQuery(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SubClass());
        insertObjectWithFactHandle(underTest, new SubClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<>(result)).hasSize(2);
    }

    @ClassAwareObjectStoreParameterizedTest
    public void onceSuperClassIsSetUpForReadingItCanBecomeSetUpForWritingWithoutGettingDuplicateQueryReturns(final ClassAwareObjectStore underTest) throws Exception {
        assertThat(underTest.iterateObjects(SuperClass.class)).isEmpty();

        insertObjectWithFactHandle(underTest, new SubClass());
        insertObjectWithFactHandle(underTest, new SuperClass());


        Collection<Object> result = collect(underTest.iterateObjects(SuperClass.class));

        assertThat(result).hasSize(2);
        // Check there's no duplication of results
        assertThat(new HashSet<>(result)).hasSize(2);
    }

    @ClassAwareObjectStoreParameterizedTest
    public void clearRemovesInsertedObjects(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SimpleClass());
        assertThat(underTest.iterateObjects()).hasSize(1);

        underTest.clear();

        assertThat(underTest.iterateObjects()).isEmpty();
    }

    @ClassAwareObjectStoreParameterizedTest
    public void canIterateOverObjectsUsingCustomFilter(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SuperClass());
        insertObjectWithFactHandle(underTest, new SubClass());

        Collection<Object> result = collect(underTest.iterateObjects((ObjectFilter) SubClass.class::isInstance));

        assertThat(result).hasSize(1);
        assertThat(result).anySatisfy(e -> assertThat(e).isInstanceOf(SubClass.class));
    }

    @ClassAwareObjectStoreParameterizedTest
    public void iteratingOverFactHandlesHasSameNumberOfResultsAsIteratingOverObjects(final ClassAwareObjectStore underTest) throws Exception {
        insertObjectWithFactHandle(underTest, new SuperClass());
        insertObjectWithFactHandle(underTest, new SubClass());

        assertThat(underTest.iterateFactHandles(SubClass.class)).hasSize(1);
        assertThat(underTest.iterateFactHandles(SuperClass.class)).hasSize(2);
    }


    private void insertObjectWithFactHandle(ClassAwareObjectStore underTest, Object objectToInsert) {
        underTest.addHandle(handleFor(objectToInsert), objectToInsert);
    }

    private static <T> Collection<T> collect(Iterator<T> objects) {
        List<T> result = new ArrayList<T>();
        while (objects.hasNext()) {
            result.add(objects.next());
        }
        return result;
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

    static Stream<ClassAwareObjectStore> dataProvider() {
        return Stream.of(new RuleBaseConfiguration() {{
                             setAssertBehaviour(AssertBehaviour.EQUALITY);
                         }},
                         new RuleBaseConfiguration() {{
                             setAssertBehaviour(AssertBehaviour.IDENTITY);
                         }})
                .map(c -> new ClassAwareObjectStore(c, new ReentrantLock()));
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ParameterizedTest
    @MethodSource("dataProvider")
    public @interface ClassAwareObjectStoreParameterizedTest {
    }
}

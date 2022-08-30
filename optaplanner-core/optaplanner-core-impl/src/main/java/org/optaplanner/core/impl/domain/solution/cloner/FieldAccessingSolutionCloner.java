package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public final class FieldAccessingSolutionCloner<Solution_> implements SolutionCloner<Solution_> {

    private final ConcurrentMap<Class<?>, Constructor<?>> constructorMemoization = new ConcurrentMemoization<>();
    private final ConcurrentMap<Class<?>, Map<Field, FieldCloner<?>>> fieldListMemoization = new ConcurrentMemoization<>();
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final DeepCloningUtils deepCloningUtils;

    public FieldAccessingSolutionCloner(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        this.deepCloningUtils = new DeepCloningUtils(solutionDescriptor);
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Solution_ cloneSolution(Solution_ originalSolution) {
        return new FieldAccessingSolutionClonerRun(originalSolution)
                .call();
    }

    /**
     * This method is thread-safe.
     *
     * @param clazz never null
     * @param <C> type
     * @return never null
     */
    @SuppressWarnings("unchecked")
    private <C> Constructor<C> retrieveCachedConstructor(Class<C> clazz) {
        return (Constructor<C>) constructorMemoization.computeIfAbsent(clazz, key -> {
            Constructor<C> constructor;
            try {
                constructor = clazz.getDeclaredConstructor();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a planning clone.", e);
            }
            constructor.setAccessible(true);
            return constructor;
        });
    }

    /**
     * This method is thread-safe.
     *
     * @param clazz never null
     * @param <C> type
     * @return never null
     */
    private <C> Map<Field, FieldCloner<?>> retrieveCachedFields(Class<C> clazz) {
        return fieldListMemoization.computeIfAbsent(clazz, key -> {
            Field[] fields = key.getDeclaredFields();
            Map<Field, FieldCloner<?>> fieldMap = new IdentityHashMap<>(fields.length);
            for (Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    fieldMap.put(field, getCloner(clazz, field));
                }
            }
            return fieldMap;
        });
    }

    private static <C> FieldCloner<C> getCloner(Class<C> clazz, Field field) {
        Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive()) {
            if (fieldType == boolean.class) {
                return BooleanFieldCloner.getInstance();
            } else if (fieldType == byte.class) {
                return ByteFieldCloner.getInstance();
            } else if (fieldType == char.class) {
                return CharFieldCloner.getInstance();
            } else if (fieldType == short.class) {
                return ShortFieldCloner.getInstance();
            } else if (fieldType == int.class) {
                return IntFieldCloner.getInstance();
            } else if (fieldType == long.class) {
                return LongFieldCloner.getInstance();
            } else if (fieldType == float.class) {
                return FloatFieldCloner.getInstance();
            } else if (fieldType == double.class) {
                return DoubleFieldCloner.getInstance();
            } else {
                throw new IllegalStateException("Impossible state: The class (" + clazz + ") has a field (" + field
                        + ") of an unknown primitive type (" + fieldType + ").");
            }
        } else if (fieldType.isEnum() || DeepCloningUtils.IMMUTABLE_CLASSES.contains(fieldType)) {
            return ShallowCloningFieldCloner.getInstance();
        } else {
            return DeepCloningFieldCloner.getInstance();
        }
    }

    private final class FieldAccessingSolutionClonerRun implements Callable<Solution_> {

        private final Solution_ originalSolution;
        private final Map<Object, Object> originalToCloneMap;
        private final Queue<Unprocessed> unprocessedQueue;

        private FieldAccessingSolutionClonerRun(Solution_ originalSolution) {
            this.originalSolution = originalSolution;
            int entityCount = solutionDescriptor.getEntityCount(originalSolution);
            this.unprocessedQueue = new ArrayDeque<>(entityCount + 1);
            this.originalToCloneMap = new IdentityHashMap<>(entityCount + 1);
        }

        @Override
        public Solution_ call() {
            Solution_ cloneSolution = clone(originalSolution);
            processQueue();
            validateCloneSolution(originalSolution, cloneSolution);
            return cloneSolution;
        }

        private <C> C clone(C original) {
            if (original == null) {
                return null;
            }
            C existingClone = (C) originalToCloneMap.get(original);
            if (existingClone != null) {
                return existingClone;
            }
            Class<C> instanceClass = (Class<C>) original.getClass();
            C clone = constructClone(instanceClass);
            originalToCloneMap.put(original, clone);
            copyFields(instanceClass, instanceClass, original, clone);
            return clone;
        }

        private <C> C constructClone(Class<C> clazz) {
            try {
                Constructor<C> constructor = retrieveCachedConstructor(clazz);
                return constructor.newInstance();
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("The class (" + clazz
                        + ") should have a no-arg constructor to create a planning clone.", e);
            }
        }

        private <C> void copyFields(Class<C> clazz, Class<? extends C> instanceClass, C original, C clone) {
            for (Map.Entry<Field, FieldCloner<?>> entry : retrieveCachedFields(clazz).entrySet()) {
                Field field = entry.getKey();
                FieldCloner<C> fieldCloner = (FieldCloner<C>) entry.getValue();
                Consumer<Object> unprocessedValueConsumer = fieldCloner.mayDeferClone()
                        ? originalValue -> unprocessedQueue.add(new Unprocessed(clone, field, originalValue))
                        : null;
                fieldCloner.clone(deepCloningUtils, field, instanceClass, original, clone, unprocessedValueConsumer);
            }
            Class<? super C> superclass = clazz.getSuperclass();
            if (superclass != null) {
                copyFields(superclass, instanceClass, original, clone);
            }
        }

        private void processQueue() {
            while (!unprocessedQueue.isEmpty()) {
                Unprocessed unprocessed = unprocessedQueue.remove();
                process(unprocessed);
            }
        }

        private void process(Unprocessed unprocessed) {
            Object cloneValue;
            Object originalValue = unprocessed.originalValue;
            Field field = unprocessed.field;
            Class<?> fieldType = field.getType();
            if (originalValue instanceof Collection) {
                cloneValue = cloneCollection(fieldType, (Collection<?>) originalValue);
            } else if (originalValue instanceof Map) {
                cloneValue = cloneMap(fieldType, (Map<?, ?>) originalValue);
            } else if (originalValue.getClass().isArray()) {
                cloneValue = cloneArray(fieldType, originalValue);
            } else {
                cloneValue = clone(originalValue);
            }
            FieldCloner.setFieldValue(unprocessed.bean, field, cloneValue);
        }

        private Object cloneArray(Class<?> expectedType, Object originalArray) {
            int arrayLength = Array.getLength(originalArray);
            Object cloneArray = Array.newInstance(originalArray.getClass().getComponentType(), arrayLength);
            if (!expectedType.isInstance(cloneArray)) {
                throw new IllegalStateException("The cloneArrayClass (" + cloneArray.getClass()
                        + ") created for originalArrayClass (" + originalArray.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (int i = 0; i < arrayLength; i++) {
                Object cloneElement = cloneCollectionsElementIfNeeded(Array.get(originalArray, i));
                Array.set(cloneArray, i, cloneElement);
            }
            return cloneArray;
        }

        private <E> Collection<E> cloneCollection(Class<?> expectedType, Collection<E> originalCollection) {
            Collection<E> cloneCollection = constructCloneCollection(originalCollection);
            if (!expectedType.isInstance(cloneCollection)) {
                throw new IllegalStateException("The cloneCollectionClass (" + cloneCollection.getClass()
                        + ") created for originalCollectionClass (" + originalCollection.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (E originalElement : originalCollection) {
                E cloneElement = cloneCollectionsElementIfNeeded(originalElement);
                cloneCollection.add(cloneElement);
            }
            return cloneCollection;
        }

        private <E> Collection<E> constructCloneCollection(Collection<E> originalCollection) {
            // TODO Don't hardcode all standard collections
            if (originalCollection instanceof List) {
                if (originalCollection instanceof ArrayList) {
                    return new ArrayList<>(originalCollection.size());
                } else if (originalCollection instanceof LinkedList) {
                    return new LinkedList<>();
                } else { // Default List
                    return new ArrayList<>(originalCollection.size());
                }
            } else if (originalCollection instanceof Set) {
                if (originalCollection instanceof SortedSet) {
                    Comparator<E> setComparator = ((SortedSet) originalCollection).comparator();
                    return new TreeSet<>(setComparator);
                } else if (originalCollection instanceof LinkedHashSet) {
                    return new LinkedHashSet<>(originalCollection.size());
                } else if (originalCollection instanceof HashSet) {
                    return new HashSet<>(originalCollection.size());
                } else { // Default Set
                    // Default to a LinkedHashSet to respect order
                    return new LinkedHashSet<>(originalCollection.size());
                }
            } else if (originalCollection instanceof Deque) {
                return new ArrayDeque<>(originalCollection.size());
            } else { // Default collection
                return new ArrayList<>(originalCollection.size());
            }
        }

        private <K, V> Map<K, V> cloneMap(Class<?> expectedType, Map<K, V> originalMap) {
            Map<K, V> cloneMap = constructCloneMap(originalMap);
            if (!expectedType.isInstance(cloneMap)) {
                throw new IllegalStateException("The cloneMapClass (" + cloneMap.getClass()
                        + ") created for originalMapClass (" + originalMap.getClass()
                        + ") is not assignable to the field's type (" + expectedType + ").\n"
                        + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
            }
            for (Map.Entry<K, V> originalEntry : originalMap.entrySet()) {
                K cloneKey = cloneCollectionsElementIfNeeded(originalEntry.getKey());
                V cloneValue = cloneCollectionsElementIfNeeded(originalEntry.getValue());
                cloneMap.put(cloneKey, cloneValue);
            }
            return cloneMap;
        }

        private <K, V> Map<K, V> constructCloneMap(Map<K, V> originalMap) {
            // Normally a Map will never be selected for cloning, but extending implementations might anyway
            if (originalMap instanceof SortedMap) {
                Comparator<K> setComparator = ((SortedMap) originalMap).comparator();
                return new TreeMap<>(setComparator);
            } else if (originalMap instanceof LinkedHashMap) {
                return new LinkedHashMap<>(originalMap.size());
            } else if (originalMap instanceof HashMap) {
                return new HashMap<>(originalMap.size());
            } else { // Default Map
                // Default to a LinkedHashMap to respect order
                return new LinkedHashMap<>(originalMap.size());
            }
        }

        private <C> C cloneCollectionsElementIfNeeded(C original) {
            // Because an element which is itself a Collection or Map might hold an entity, we clone it too
            // Also, the List<Long> in Map<String, List<Long>> needs to be cloned
            // if the List<Long> is a shadow, despite that Long never needs to be cloned (because it's immutable).
            if (original instanceof Collection) {
                return (C) cloneCollection(Collection.class, (Collection) original);
            } else if (original instanceof Map) {
                return (C) cloneMap(Map.class, (Map) original);
            } else if (original.getClass().isArray()) {
                return (C) cloneArray(original.getClass(), original);
            }
            if (deepCloningUtils.retrieveDeepCloneDecisionForActualValueClass(original.getClass())) {
                return clone(original);
            } else {
                return original;
            }
        }

        /**
         * Fails fast if {@link DeepCloningUtils#isFieldAnEntityPropertyOnSolution} assumptions were wrong.
         *
         * @param originalSolution never null
         * @param cloneSolution never null
         */
        private void validateCloneSolution(Solution_ originalSolution, Solution_ cloneSolution) {
            for (MemberAccessor memberAccessor : solutionDescriptor.getEntityMemberAccessorMap().values()) {
                validateCloneProperty(originalSolution, cloneSolution, memberAccessor);
            }
            for (MemberAccessor memberAccessor : solutionDescriptor.getEntityCollectionMemberAccessorMap().values()) {
                validateCloneProperty(originalSolution, cloneSolution, memberAccessor);
            }
        }

        private void validateCloneProperty(Solution_ originalSolution, Solution_ cloneSolution, MemberAccessor memberAccessor) {
            Object originalProperty = memberAccessor.executeGetter(originalSolution);
            if (originalProperty != null) {
                Object cloneProperty = memberAccessor.executeGetter(cloneSolution);
                if (originalProperty == cloneProperty) {
                    throw new IllegalStateException(
                            "The solutionProperty (" + memberAccessor.getName() + ") was not cloned as expected."
                                    + " The " + FieldAccessingSolutionCloner.class.getSimpleName() + " failed to recognize"
                                    + " that property's field, probably because its field name is different.");
                }
            }
        }
    }

    private final static class Unprocessed {

        final Object bean;
        final Field field;
        final Object originalValue;

        public Unprocessed(Object bean, Field field, Object originalValue) {
            this.bean = bean;
            this.field = field;
            this.originalValue = originalValue;
        }

    }

}

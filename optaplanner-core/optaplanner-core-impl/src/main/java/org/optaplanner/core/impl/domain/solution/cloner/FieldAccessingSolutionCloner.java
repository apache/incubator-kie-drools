package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
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
import java.util.concurrent.ConcurrentMap;

import org.optaplanner.core.api.domain.solution.cloner.SolutionCloner;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

/**
 * @implNote This class is thread-safe.
 */
public final class FieldAccessingSolutionCloner<Solution_> implements SolutionCloner<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private final ConcurrentMap<Class<?>, Constructor<?>> constructorMemoization = new ConcurrentMemoization<>();
    private final ConcurrentMap<Class<?>, ClassMetadata> classMetadataMemoization = new ConcurrentMemoization<>();

    public FieldAccessingSolutionCloner(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    @Override
    public Solution_ cloneSolution(Solution_ originalSolution) {
        int entityCount = solutionDescriptor.getEntityCount(originalSolution);
        Map<Object, Object> originalToCloneMap = new IdentityHashMap<>(entityCount + 1);
        Queue<Unprocessed> unprocessedQueue = new ArrayDeque<>(entityCount + 1);
        Solution_ cloneSolution = clone(originalSolution, originalToCloneMap, unprocessedQueue,
                retrieveClassMetadata(originalSolution.getClass()));
        while (!unprocessedQueue.isEmpty()) {
            Unprocessed unprocessed = unprocessedQueue.remove();
            Object cloneValue = process(unprocessed, originalToCloneMap, unprocessedQueue);
            FieldCloningUtils.setObjectFieldValue(unprocessed.bean, unprocessed.field, cloneValue);
        }
        validateCloneSolution(originalSolution, cloneSolution);
        return cloneSolution;
    }

    private Object process(Unprocessed unprocessed, Map<Object, Object> originalToCloneMap,
            Queue<Unprocessed> unprocessedQueue) {
        Object originalValue = unprocessed.originalValue;
        Field field = unprocessed.field;
        Class<?> fieldType = field.getType();
        if (originalValue instanceof Collection) {
            return cloneCollection(fieldType, (Collection<?>) originalValue, originalToCloneMap, unprocessedQueue);
        } else if (originalValue instanceof Map) {
            return cloneMap(fieldType, (Map<?, ?>) originalValue, originalToCloneMap, unprocessedQueue);
        } else if (originalValue.getClass().isArray()) {
            return cloneArray(fieldType, originalValue, originalToCloneMap, unprocessedQueue);
        } else {
            return clone(originalValue, originalToCloneMap, unprocessedQueue,
                    retrieveClassMetadata(originalValue.getClass()));
        }
    }

    private <C> C clone(C original, Map<Object, Object> originalToCloneMap, Queue<Unprocessed> unprocessedQueue,
            ClassMetadata declaringClassMetadata) {
        if (original == null) {
            return null;
        }
        C existingClone = (C) originalToCloneMap.get(original);
        if (existingClone != null) {
            return existingClone;
        }
        Class<C> declaringClass = (Class<C>) original.getClass();
        C clone = constructClone(declaringClass);
        originalToCloneMap.put(original, clone);
        copyFields(declaringClass, original, clone, unprocessedQueue, declaringClassMetadata);
        return clone;
    }

    private <C> C constructClone(Class<C> clazz) {
        try {
            return (C) constructorMemoization.computeIfAbsent(clazz, key -> {
                try {
                    Constructor<C> constructor = (Constructor<C>) key.getDeclaredConstructor();
                    constructor.setAccessible(true);
                    return constructor;
                } catch (ReflectiveOperationException e) {
                    throw new IllegalStateException("The class (" + key
                            + ") should have a no-arg constructor to create a planning clone.", e);
                }
            }).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("The class (" + clazz
                    + ") should have a no-arg constructor to create a planning clone.", e);
        }
    }

    private <C> void copyFields(Class<C> clazz, C original, C clone, Queue<Unprocessed> unprocessedQueue,
            ClassMetadata declaringClassMetadata) {
        for (ShallowCloningFieldCloner fieldCloner : declaringClassMetadata.getCopiedFieldArray()) {
            fieldCloner.clone(original, clone);
        }
        for (DeepCloningFieldCloner fieldCloner : declaringClassMetadata.getClonedFieldArray()) {
            Object unprocessedValue = fieldCloner.clone(solutionDescriptor, original, clone);
            if (unprocessedValue != null) {
                unprocessedQueue.add(new Unprocessed(clone, fieldCloner.getField(), unprocessedValue));
            }
        }
        Class<? super C> superclass = clazz.getSuperclass();
        if (superclass != null && superclass != Object.class) {
            copyFields(superclass, original, clone, unprocessedQueue, retrieveClassMetadata(superclass));
        }
    }

    private Object cloneArray(Class<?> expectedType, Object originalArray, Map<Object, Object> originalToCloneMap,
            Queue<Unprocessed> unprocessedQueue) {
        int arrayLength = Array.getLength(originalArray);
        Object cloneArray = Array.newInstance(originalArray.getClass().getComponentType(), arrayLength);
        if (!expectedType.isInstance(cloneArray)) {
            throw new IllegalStateException("The cloneArrayClass (" + cloneArray.getClass()
                    + ") created for originalArrayClass (" + originalArray.getClass()
                    + ") is not assignable to the field's type (" + expectedType + ").\n"
                    + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
        }
        for (int i = 0; i < arrayLength; i++) {
            Object cloneElement =
                    cloneCollectionsElementIfNeeded(Array.get(originalArray, i), originalToCloneMap, unprocessedQueue);
            Array.set(cloneArray, i, cloneElement);
        }
        return cloneArray;
    }

    private <E> Collection<E> cloneCollection(Class<?> expectedType, Collection<E> originalCollection,
            Map<Object, Object> originalToCloneMap, Queue<Unprocessed> unprocessedQueue) {
        Collection<E> cloneCollection = constructCloneCollection(originalCollection);
        if (!expectedType.isInstance(cloneCollection)) {
            throw new IllegalStateException("The cloneCollectionClass (" + cloneCollection.getClass()
                    + ") created for originalCollectionClass (" + originalCollection.getClass()
                    + ") is not assignable to the field's type (" + expectedType + ").\n"
                    + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
        }
        for (E originalElement : originalCollection) {
            E cloneElement = cloneCollectionsElementIfNeeded(originalElement, originalToCloneMap, unprocessedQueue);
            cloneCollection.add(cloneElement);
        }
        return cloneCollection;
    }

    private static <E> Collection<E> constructCloneCollection(Collection<E> originalCollection) {
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

    private <K, V> Map<K, V> cloneMap(Class<?> expectedType, Map<K, V> originalMap, Map<Object, Object> originalToCloneMap,
            Queue<Unprocessed> unprocessedQueue) {
        Map<K, V> cloneMap = constructCloneMap(originalMap);
        if (!expectedType.isInstance(cloneMap)) {
            throw new IllegalStateException("The cloneMapClass (" + cloneMap.getClass()
                    + ") created for originalMapClass (" + originalMap.getClass()
                    + ") is not assignable to the field's type (" + expectedType + ").\n"
                    + "Maybe consider replacing the default " + SolutionCloner.class.getSimpleName() + ".");
        }
        for (Map.Entry<K, V> originalEntry : originalMap.entrySet()) {
            K cloneKey = cloneCollectionsElementIfNeeded(originalEntry.getKey(), originalToCloneMap, unprocessedQueue);
            V cloneValue = cloneCollectionsElementIfNeeded(originalEntry.getValue(), originalToCloneMap, unprocessedQueue);
            cloneMap.put(cloneKey, cloneValue);
        }
        return cloneMap;
    }

    private static <K, V> Map<K, V> constructCloneMap(Map<K, V> originalMap) {
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

    private ClassMetadata retrieveClassMetadata(Class<?> declaringClass) {
        return classMetadataMemoization.computeIfAbsent(declaringClass, ClassMetadata::new);
    }

    private <C> C cloneCollectionsElementIfNeeded(C original, Map<Object, Object> originalToCloneMap,
            Queue<Unprocessed> unprocessedQueue) {
        if (original == null) {
            return null;
        }
        // Because an element which is itself a Collection or Map might hold an entity, we clone it too
        // Also, the List<Long> in Map<String, List<Long>> needs to be cloned
        // if the List<Long> is a shadow, despite that Long never needs to be cloned (because it's immutable).
        if (original instanceof Collection) {
            return (C) cloneCollection(Collection.class, (Collection) original, originalToCloneMap, unprocessedQueue);
        } else if (original instanceof Map) {
            return (C) cloneMap(Map.class, (Map) original, originalToCloneMap, unprocessedQueue);
        } else if (original.getClass().isArray()) {
            return (C) cloneArray(original.getClass(), original, originalToCloneMap, unprocessedQueue);
        }
        ClassMetadata classMetadata = retrieveClassMetadata(original.getClass());
        if (classMetadata.isDeepCloned) {
            return clone(original, originalToCloneMap, unprocessedQueue, classMetadata);
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

    private static <Solution_> void validateCloneProperty(Solution_ originalSolution, Solution_ cloneSolution,
            MemberAccessor memberAccessor) {
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

    private final class ClassMetadata {

        private final Class<?> declaringClass;
        private final boolean isDeepCloned;

        /**
         * Contains one cloner for every field that needs to be shallow cloned (= copied).
         */
        private ShallowCloningFieldCloner[] copiedFieldArray;
        /**
         * Contains one cloner for every field that needs to be deep-cloned.
         */
        private DeepCloningFieldCloner[] clonedFieldArray;

        public ClassMetadata(Class<?> declaringClass) {
            this.declaringClass = declaringClass;
            this.isDeepCloned = DeepCloningUtils.isClassDeepCloned(solutionDescriptor, declaringClass);
        }

        public ShallowCloningFieldCloner[] getCopiedFieldArray() {
            if (copiedFieldArray == null) { // Lazy-loaded; some types (such as String) will never get here.
                copiedFieldArray = Arrays.stream(declaringClass.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .filter(field -> DeepCloningUtils.isImmutable(field.getType()))
                        .peek(f -> f.setAccessible(true))
                        .map(ShallowCloningFieldCloner::of)
                        .toArray(ShallowCloningFieldCloner[]::new);
            }
            return copiedFieldArray;
        }

        public DeepCloningFieldCloner[] getClonedFieldArray() {
            if (clonedFieldArray == null) { // Lazy-loaded; some types (such as String) will never get here.
                clonedFieldArray = Arrays.stream(declaringClass.getDeclaredFields())
                        .filter(f -> !Modifier.isStatic(f.getModifiers()))
                        .filter(field -> !DeepCloningUtils.isImmutable(field.getType()))
                        .peek(f -> f.setAccessible(true))
                        .map(DeepCloningFieldCloner::new)
                        .toArray(DeepCloningFieldCloner[]::new);
            }
            return clonedFieldArray;
        }

    }

    private static final class Unprocessed {

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

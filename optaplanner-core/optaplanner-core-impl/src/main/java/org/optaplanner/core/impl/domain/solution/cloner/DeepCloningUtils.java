package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.solution.cloner.DeepPlanningClone;
import org.optaplanner.core.api.domain.variable.PlanningListVariable;
import org.optaplanner.core.api.score.buildin.bendable.BendableScore;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.buildin.bendablelong.BendableLongScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftbigdecimal.HardMediumSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplelong.SimpleLongScore;
import org.optaplanner.core.impl.domain.common.ReflectionHelper;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.util.Pair;

public final class DeepCloningUtils {

    // Instances of these classes will never be deep-cloned.
    public static final Set<Class<?>> IMMUTABLE_CLASSES = Set.of(
            // Numbers
            Byte.class, Short.class, Integer.class, Long.class, Float.class, Double.class, BigInteger.class, BigDecimal.class,
            // Optional
            Optional.class, OptionalInt.class, OptionalLong.class, OptionalDouble.class,
            // Date and time
            Duration.class, Instant.class, LocalDate.class, LocalDateTime.class, LocalTime.class, MonthDay.class,
            OffsetDateTime.class, OffsetTime.class, Period.class, Year.class, YearMonth.class, ZonedDateTime.class,
            ZoneId.class, ZoneOffset.class,
            // Others
            Boolean.class, Character.class, String.class, UUID.class,
            // Score
            SimpleScore.class, SimpleLongScore.class, SimpleBigDecimalScore.class,
            HardSoftScore.class, HardSoftLongScore.class, HardSoftBigDecimalScore.class,
            HardMediumSoftScore.class, HardMediumSoftLongScore.class, HardMediumSoftBigDecimalScore.class,
            BendableScore.class, BendableLongScore.class, BendableBigDecimalScore.class);
    private final SolutionDescriptor<?> solutionDescriptor;
    private final ConcurrentMap<Pair<Field, Class<?>>, Boolean> fieldDeepClonedMemoization;
    private final ConcurrentMap<Class<?>, Boolean> actualValueClassDeepClonedMemoization;

    public DeepCloningUtils(SolutionDescriptor<?> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
        fieldDeepClonedMemoization = new ConcurrentMemoization<>();
        actualValueClassDeepClonedMemoization = new ConcurrentMemoization<>();
    }

    // owningClass != declaringClass
    // owningClass refers to the actual instance class, which may be a subclass of the declaring class

    /**
     * Gets the deep cloning decision for a particular value assigned to a field,
     * memoizing the result.
     *
     * @param field the field to get the deep cloning decision of
     * @param owningClass the class that owns the field; can be different
     *        from the field's declaring class (ex: subclass)
     * @param actualValueClass the class of the value that is currently assigned
     *        to the field; can be different from the field type
     *        (ex: for the field "List myList", the actual value
     *        class might be ArrayList).
     * @return true iff the field should be deep cloned with a particular value.
     */
    public boolean getDeepCloneDecision(Field field, Class<?> owningClass, Class<?> actualValueClass) {
        Pair<Field, Class<?>> pair = Pair.of(field, owningClass);
        Boolean deepCloneDecision = fieldDeepClonedMemoization.computeIfAbsent(pair,
                key -> isFieldDeepCloned(field, owningClass));
        return deepCloneDecision || actualValueClassDeepClonedMemoization.computeIfAbsent(actualValueClass,
                key -> isClassDeepCloned(actualValueClass));
    }

    /**
     * This method is thread-safe.
     *
     * @param actualValueClass never null
     * @return never null
     */
    public boolean retrieveDeepCloneDecisionForActualValueClass(Class<?> actualValueClass) {
        return actualValueClassDeepClonedMemoization.computeIfAbsent(actualValueClass,
                key -> isClassDeepCloned(actualValueClass));
    }

    /**
     * Gets the deep cloning decision for a field.
     *
     * @param field The field to get the deep cloning decision of
     * @param owningClass The class that owns the field; can be different
     *        from the field's declaring class (ex: subclass).
     * @return True iff the field should always be deep cloned (regardless of value).
     */
    public boolean isFieldDeepCloned(Field field, Class<?> owningClass) {
        Class<?> fieldType = field.getType();
        if (fieldType.isPrimitive() || fieldType.isEnum()) {
            return false;
        } else if (IMMUTABLE_CLASSES.contains(fieldType)) {
            return false;
        }
        return isFieldAnEntityPropertyOnSolution(field, owningClass)
                || isFieldAnEntityOrSolution(field)
                || isFieldAPlanningListVariable(field, owningClass)
                || isFieldADeepCloneProperty(field, owningClass);
    }

    /**
     * Return true only if a field represent an entity property on the solution class.
     * An entity property is one who type is a PlanningEntity or a collection
     * of PlanningEntity.
     *
     * @param field The field to get the deep cloning decision of
     * @param owningClass The class that owns the field; can be different
     *        from the field's declaring class (ex: subclass).
     * @return True only if the field is an entity property on the solution class.
     *         May return false if the field getter/setter is complex.
     */
    public boolean isFieldAnEntityPropertyOnSolution(Field field,
            Class<?> owningClass) {
        if (!solutionDescriptor.getSolutionClass().isAssignableFrom(owningClass)) {
            return false;
        }

        // field.getDeclaringClass() is a superclass of or equal to the owningClass
        String fieldName = field.getName();
        // This assumes we're dealing with a simple getter/setter.
        // If that assumption is false, validateCloneSolution(...) fails-fast.
        if (solutionDescriptor.getEntityMemberAccessorMap().get(fieldName) != null) {
            return true;
        }
        // This assumes we're dealing with a simple getter/setter.
        // If that assumption is false, validateCloneSolution(...) fails-fast.
        return solutionDescriptor.getEntityCollectionMemberAccessorMap().get(fieldName) != null;
    }

    /**
     * Returns true iff a field represent an Entity/Solution or a collection
     * of Entity/Solution.
     *
     * @param field The field to get the deep cloning decision of
     * @return True only if the field represents or contains a PlanningEntity or PlanningSolution
     */
    public boolean isFieldAnEntityOrSolution(Field field) {
        Class<?> type = field.getType();
        if (isClassDeepCloned(type)) {
            return true;
        }
        if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type)) {
            if (isTypeArgumentDeepCloned(field.getGenericType())) {
                return true;
            }
        } else if (type.isArray()) {
            if (isClassDeepCloned(type.getComponentType())) {
                return true;
            }
        }
        return false;
    }

    public boolean isClassDeepCloned(Class<?> type) {
        if (IMMUTABLE_CLASSES.contains(type)) {
            return false;
        }
        return solutionDescriptor.hasEntityDescriptor(type)
                || solutionDescriptor.getSolutionClass().isAssignableFrom(type)
                || type.isAnnotationPresent(DeepPlanningClone.class);
    }

    public boolean isTypeArgumentDeepCloned(Type genericType) {
        // Check the generic type arguments of the field.
        // It is possible for fields and methods, but not instances.
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
                if (actualTypeArgument instanceof Class
                        && isClassDeepCloned((Class) actualTypeArgument)) {
                    return true;
                }
                if (isTypeArgumentDeepCloned(actualTypeArgument)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isFieldADeepCloneProperty(Field field, Class<?> owningClass) {
        if (field.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        Method getterMethod = ReflectionHelper.getGetterMethod(owningClass, field.getName());
        if (getterMethod != null && getterMethod.isAnnotationPresent(DeepPlanningClone.class)) {
            return true;
        }
        return false;
    }

    public boolean isFieldAPlanningListVariable(Field field, Class<?> owningClass) {
        if (field.isAnnotationPresent(PlanningListVariable.class)) {
            return true;
        }
        Method getterMethod = ReflectionHelper.getGetterMethod(owningClass, field.getName());
        if (getterMethod != null && getterMethod.isAnnotationPresent(PlanningListVariable.class)) {
            return true;
        }
        return false;
    }

    /**
     * @return never null
     */
    public Set<Class<?>> getDeepClonedTypeArguments(Type genericType) {
        // Check the generic type arguments of the field.
        // It is possible for fields and methods, but not instances.
        if (!(genericType instanceof ParameterizedType)) {
            return Collections.emptySet();
        }

        Set<Class<?>> deepClonedTypeArguments = new HashSet<>();
        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        for (Type actualTypeArgument : parameterizedType.getActualTypeArguments()) {
            if (actualTypeArgument instanceof Class
                    && isClassDeepCloned((Class) actualTypeArgument)) {
                deepClonedTypeArguments.add((Class) actualTypeArgument);
            }
            deepClonedTypeArguments.addAll(getDeepClonedTypeArguments(actualTypeArgument));
        }
        return deepClonedTypeArguments;
    }

    public Set<Class<?>> getDeepClonedClasses(Collection<Class<?>> entitySubclasses) {
        Set<Class<?>> deepClonedClassSet = new HashSet<>();

        Stream.of(Stream.of(solutionDescriptor.getSolutionClass()),
                solutionDescriptor.getEntityClassSet().stream(),
                entitySubclasses.stream())
                .flatMap(classStream -> classStream)
                .forEach(clazz -> {
                    deepClonedClassSet.add(clazz);
                    for (Field field : getAllFields(clazz)) {
                        deepClonedClassSet.addAll(getDeepClonedTypeArguments(field.getGenericType()));
                        if (isFieldDeepCloned(field, clazz)) {
                            deepClonedClassSet.add(field.getType());
                        }
                    }
                });

        return deepClonedClassSet;
    }

    private static List<Field> getAllFields(Class<?> baseClass) {
        Class<?> clazz = baseClass;
        Stream<Field> memberStream = Stream.empty();
        while (clazz != null) {
            Stream<Field> fieldStream = Stream.of(clazz.getDeclaredFields());
            memberStream = Stream.concat(memberStream, fieldStream);
            clazz = clazz.getSuperclass();
        }
        return memberStream.collect(Collectors.toList());
    }
}

package org.optaplanner.core.impl.domain.solution.cloner;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @implNote This class is thread-safe.
 */
final class DeepCloningFieldCloner implements FieldCloner {

    private final AtomicReference<Metadata> valueDeepCloneDecision = new AtomicReference<>();
    private final AtomicInteger fieldDeepCloneDecision = new AtomicInteger(-1);
    private final Field field;

    public DeepCloningFieldCloner(Field field) {
        this.field = Objects.requireNonNull(field);
    }

    @Override
    public <C> Unprocessed clone(DeepCloningUtils deepCloningUtils, C original, C clone) {
        Object originalValue = FieldCloner.getGenericFieldValue(original, field);
        if (deepClone(deepCloningUtils, original.getClass(), originalValue)) { // Defer filling in the field.
            return new Unprocessed(clone, field, originalValue);
        } else { // Shallow copy.
            FieldCloner.setGenericFieldValue(clone, field, originalValue);
            return null;
        }
    }

    /**
     * Obtaining the decision on whether or not to deep-clone is expensive.
     * This method exists to cache those computations as much as possible,
     * while maintaining thread-safety.
     *
     * @param deepCloningUtils never null
     * @param fieldTypeClass never null
     * @param originalValue never null
     * @return true if the value needs to be deep-cloned
     */
    private boolean deepClone(DeepCloningUtils deepCloningUtils, Class<?> fieldTypeClass, Object originalValue) {
        if (originalValue == null) {
            return false;
        }
        /*
         * This caching mechanism takes advantage of the fact that, for a particular field on a particular class,
         * the types of values contained are unlikely to change and therefore it is safe to cache the calculation.
         * In the unlikely event of a cache miss, we recompute.
         */
        boolean isValueDeepCloned = valueDeepCloneDecision.updateAndGet(old -> {
            Class<?> originalClass = originalValue.getClass();
            if (old == null || old.clz != originalClass) {
                return new Metadata(originalClass, deepCloningUtils.isClassDeepCloned(originalClass));
            } else {
                return old;
            }
        }).decision;
        if (isValueDeepCloned) { // The value has to be deep-cloned. Does not matter what the field says.
            return true;
        }
        /*
         * The decision to clone a field is constant once it has been made.
         * The fieldTypeClass is guaranteed to not change for the particular field.
         */
        if (fieldDeepCloneDecision.get() < 0) {
            fieldDeepCloneDecision.set(deepCloningUtils.isFieldDeepCloned(field, fieldTypeClass) ? 1 : 0);
        }
        return fieldDeepCloneDecision.get() == 1;
    }

    private static final class Metadata {

        private final Class<?> clz;
        private final boolean decision;

        public Metadata(Class<?> clz, boolean decision) {
            this.clz = clz;
            this.decision = decision;
        }

    }
}

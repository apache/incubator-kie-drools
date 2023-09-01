package org.drools.core.common;

import java.util.Collection;
import java.util.function.BiFunction;

import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.rule.consequence.InternalMatch;
import org.kie.api.runtime.rule.FactHandle;

/**
 * The Truth Maintenance System is responsible for tracking two things. Firstly
 * It maintains a Map to track the classes with the same Equality, using the
 * EqualityKey. The EqualityKey has an internal data structure which references
 * all the handles which are equal. Secondly It maintains another map tracking
 * the justifications for logically asserted facts.
 */
public interface TruthMaintenanceSystem {

    int getEqualityKeysSize();

    Collection<EqualityKey> getEqualityKeys();

    void put(final EqualityKey key);
    EqualityKey get(Object object);
    void remove(final EqualityKey key);

    InternalFactHandle insert(Object object, Object tmsValue, InternalMatch internalMatch);
    InternalFactHandle insertPositive(Object object, InternalMatch internalMatch);
    void delete(FactHandle fh);

    void readLogicalDependency(InternalFactHandle handle, Object object, Object value, InternalMatch internalMatch, ObjectTypeConf typeConf);

    void clear();

    InternalFactHandle insertOnTms(Object object, ObjectTypeConf typeConf, PropagationContext propagationContext,
                                   InternalFactHandle handle, BiFunction<Object, ObjectTypeConf, InternalFactHandle> fhFactory);

    void updateOnTms(InternalFactHandle handle, Object object, InternalMatch internalMatch);

    void deleteFromTms(InternalFactHandle handle, EqualityKey key, PropagationContext propagationContext );


}

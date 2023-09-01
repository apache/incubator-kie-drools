package org.drools.core.base;

import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.rule.accessor.WriteAccessor;
import org.kie.api.internal.utils.KieService;

import static org.drools.base.base.CoreComponentsBuilder.throwExceptionForMissingMvel;

public interface FieldAccessorFactory extends KieService {
    class Holder {
        private static final FieldAccessorFactory fieldFactory = getFactory();

        private static FieldAccessorFactory getFactory() {
            FieldAccessorFactory instance = KieService.load( FieldAccessorFactory.class );
            return instance != null ? instance : throwExceptionForMissingMvel();
        }
    }

    static FieldAccessorFactory get() {
        return Holder.fieldFactory;
    }

    ReadAccessor getClassFieldReader(Class< ? > clazz, String fieldName, ClassFieldAccessorCache.CacheEntry cache);
    WriteAccessor getClassFieldWriter(Class< ? > clazz, String fieldName, ClassFieldAccessorCache.CacheEntry cache);
}

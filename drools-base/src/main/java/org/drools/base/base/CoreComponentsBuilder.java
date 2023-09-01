package org.drools.base.base;

import java.io.IOException;

import org.drools.base.common.MissingDependencyException;
import org.drools.base.rule.DialectRuntimeData;
import org.drools.base.rule.accessor.ReadAccessor;
import org.drools.base.util.Drools;
import org.drools.base.util.MVELExecutor;
import org.kie.api.internal.utils.KieService;

public interface CoreComponentsBuilder extends KieService {

    String NO_MVEL = "You're trying to compile a Drools asset without mvel. Please add the module org.drools:drools-mvel to your classpath.";

    static <T> T throwExceptionForMissingMvel() {
        if (Drools.isNativeImage()) {
            return null;
        }
        throw new MissingDependencyException(NO_MVEL);
    }

    class Holder {
        private static final CoreComponentsBuilder cBuilder = KieService.load( CoreComponentsBuilder.class );
    }

    static CoreComponentsBuilder get() {
        return Holder.cBuilder != null ? Holder.cBuilder : throwExceptionForMissingMvel();
    }

    static boolean present() {
        return Holder.cBuilder != null;
    }

    ReadAccessor getReadAcessor(String className, String expr, boolean typesafe, Class<?> returnType );

    Object evaluateMvelExpression(DialectRuntimeData data, ClassLoader classLoader, String expr );

    default ClassFieldInspector createClassFieldInspector( final Class<?> classUnderInspection ) throws IOException {
        return createClassFieldInspector( classUnderInspection, true );
    }

    ClassFieldInspector createClassFieldInspector( Class<?> classUnderInspection, boolean includeFinalMethods ) throws IOException;

    MVELExecutor getMVELExecutor();
}

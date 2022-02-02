/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.base;

import java.io.IOException;

import org.drools.core.common.MissingDependencyException;
import org.drools.core.rule.DialectRuntimeData;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.Drools;
import org.drools.core.util.MVELExecutor;
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

    InternalReadAccessor getReadAcessor( String className, String expr, boolean typesafe, Class<?> returnType );

    Object evaluateMvelExpression( DialectRuntimeData data, ClassLoader classLoader, String expr );

    default ClassFieldInspector createClassFieldInspector( final Class<?> classUnderInspection ) throws IOException {
        return createClassFieldInspector( classUnderInspection, true );
    }

    ClassFieldInspector createClassFieldInspector( Class<?> classUnderInspection, boolean includeFinalMethods ) throws IOException;

    MVELExecutor getMVELExecutor();
}

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

import org.drools.core.rule.DialectRuntimeData;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.util.MVELExecutor;
import org.kie.api.internal.utils.ServiceRegistry;

public interface CoreComponentsBuilder {

    boolean IS_NATIVE_IMAGE = System.getProperty("org.graalvm.nativeimage.imagecode") != null;

    String NO_MVEL = "You're trying to compile a Drools asset without mvel. Please add the module org.drools:drools-mvel to your classpath.";

    static <T> T throwExceptionForMissingMvel() {
        if (IS_NATIVE_IMAGE) {
            return null;
        }
        throw new RuntimeException(NO_MVEL);
    }

    class Holder {
        private static final CoreComponentsBuilder cBuilder = ServiceRegistry.getService( CoreComponentsBuilder.class );
    }

    static CoreComponentsBuilder get() {
        return Holder.cBuilder != null ? Holder.cBuilder : throwExceptionForMissingMvel();
    }

    static boolean present() {
        return Holder.cBuilder != null;
    }

    static boolean isNativeImage() {
        return IS_NATIVE_IMAGE;
    }

    InternalReadAccessor getReadAcessor( String className, String expr, boolean typesafe, Class<?> returnType );

    Object evaluateMvelExpression( DialectRuntimeData data, ClassLoader classLoader, String expr );

    default ClassFieldInspector createClassFieldInspector( final Class<?> classUnderInspection ) throws IOException {
        return createClassFieldInspector( classUnderInspection, true );
    }

    ClassFieldInspector createClassFieldInspector( Class<?> classUnderInspection, boolean includeFinalMethods ) throws IOException;

    MVELExecutor getMVELExecutor();
}

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

package org.drools.modelcompiler.constraints;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.drools.core.base.CoreComponentsBuilder;
import org.drools.core.util.ClassUtils;
import org.drools.model.functions.Function1;

import static org.drools.core.util.Drools.hasMvel;

public class LambdaFieldReader implements Function1 {

    private final Method accessor;
    private final String field;

    public LambdaFieldReader( Class<?> clazz, String field ) {
        this.accessor = ClassUtils.getAccessor( clazz, field );
        this.field = field;
    }

    @Override
    public Object apply( Object o ) {
        try {
            if (accessor != null) {
                return accessor.invoke( o );
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException( e );
        }
        if (!hasMvel()) {
            throw new RuntimeException("Complex timestamp expressions can be used only with drools-mvel on classpath");
        }
        return CoreComponentsBuilder.get().getMVELExecutor().eval( field, o );
    }
}

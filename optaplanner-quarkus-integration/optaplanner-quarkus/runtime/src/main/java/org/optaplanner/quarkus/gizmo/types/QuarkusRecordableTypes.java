/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.quarkus.gizmo.types;

import java.lang.reflect.Type;

import org.jboss.jandex.IndexView;
import org.jboss.jandex.PrimitiveType;

/*
 * When bytecode is being recorded in Quarkus (i.e. an
 * object is passed to a recorder), the following restrictions apply:
 *
 * - Classes without public getters/setters for all fields,
 * a constructor annotated with @RecordableConstructor with parameter names that match field names or a
 * registered substitution cannot be recorded. (https://quarkus.io/guides/writing-extensions#bytecode-recording)
 *
 * Annotations and Generic Types do not satisfy the above criteria, so they cannot be
 * recorded directly at build time. However, we don't want to use reflection at runtime,
 * and we want to build the GizmoMemberAccessors at build time. So we need to create
 * wrappers for the annotations and types so they can be recorded.
 */
public class QuarkusRecordableTypes {

    public static Type getQuarkusRecorderFriendlyType(org.jboss.jandex.Type jandexType, IndexView indexView) {
        if (jandexType == null) {
            return null;
        }

        switch (jandexType.kind()) {
            case CLASS:
                try {
                    // Classes do not need a wrapper, so we can just return the Class object
                    return Class.forName(jandexType.asClassType().name().toString(), false,
                            Thread.currentThread().getContextClassLoader());
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(
                            "Unable to find class (" + jandexType.asClassType().name() + "). Maybe check your classpath?",
                            e);
                }
            case ARRAY:
                return new QuarkusRecordableArrayType(
                        getQuarkusRecorderFriendlyType(jandexType.asArrayType().component(), indexView));
            case PRIMITIVE:
                return lookupPrimitiveType(jandexType.asPrimitiveType().primitive());
            case VOID:
                return void.class;
            case TYPE_VARIABLE:
                return new QuarkusRecordableTypeVariable(jandexType.asTypeVariable(), indexView);
            case UNRESOLVED_TYPE_VARIABLE:
                return new QuarkusRecordableTypeVariable(jandexType.asUnresolvedTypeVariable(), indexView);
            case WILDCARD_TYPE:
                return new QuarkusRecordableWildcardType(jandexType.asWildcardType(), indexView);
            case PARAMETERIZED_TYPE:
                return new QuarkusRecordableParameterizedType(jandexType.asParameterizedType(), indexView);
            default:
                throw new IllegalArgumentException("Unhandled case (" + jandexType.kind() + ").");
        }
    }

    private static Type lookupPrimitiveType(PrimitiveType.Primitive primitive) {
        switch (primitive) {
            case BYTE:
                return byte.class;
            case CHAR:
                return char.class;
            case DOUBLE:
                return double.class;
            case FLOAT:
                return float.class;
            case INT:
                return int.class;
            case LONG:
                return long.class;
            case SHORT:
                return short.class;
            case BOOLEAN:
                return boolean.class;
        }
        throw new IllegalStateException();
    }

    private QuarkusRecordableTypes() {

    }
}

/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.persistence.proto;

import org.kie.kogito.codegen.GeneratedFile;
import org.kie.kogito.codegen.GeneratedFileType;

import java.util.Collection;
import java.util.Date;

public interface ProtoGenerator {

    GeneratedFileType PROTO_TYPE = GeneratedFileType.of("PROTO", GeneratedFileType.Category.RESOURCE);
    String INDEX_COMMENT = "@Field(store = Store.YES) @SortableField";

    Proto protoOfDataClasses(String packageName, String... headers);

    Collection<GeneratedFile> generateProtoFiles();

    /**
     * Returns params of first constructor of persistence class
     * @return
     */
    Collection<String> getPersistenceClassParams();

    default String applicabilityByType(String type) {
        if (type.equals("Collection")) {
            return "repeated";
        }

        return "optional";
    }

    default String protoType(String type) {

        if (String.class.getCanonicalName().equals(type) || String.class.getSimpleName().equalsIgnoreCase(type)) {
            return "string";
        } else if (Integer.class.getCanonicalName().equals(type) || "int".equalsIgnoreCase(type)) {
            return "int32";
        } else if (Long.class.getCanonicalName().equals(type) || "long".equalsIgnoreCase(type)) {
            return "int64";
        } else if (Double.class.getCanonicalName().equals(type) || "double".equalsIgnoreCase(type)) {
            return "double";
        } else if (Float.class.getCanonicalName().equals(type) || "float".equalsIgnoreCase(type)) {
            return "float";
        } else if (Boolean.class.getCanonicalName().equals(type) || "boolean".equalsIgnoreCase(type)) {
            return "bool";
        } else if (Date.class.getCanonicalName().equals(type) || "date".equalsIgnoreCase(type)) {
            return "kogito.Date";
        }

        return null;
    }

    interface Builder<E, T extends ProtoGenerator> {

        Builder<E, T> withPersistenceClass(E persistenceClass);

        Builder<E, T> withDataClasses(Collection<E> dataClasses);

        T build(Collection<E> dataClasses);
    }
}

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

import java.util.Collection;
import java.util.Date;

public interface ProtoGenerator<T> {
    
    static final String INDEX_COMMENT = "@Field(store = Store.YES, analyze = Analyze.YES)"; 

    Proto generate(String packageName, Collection<T> dataModel, String... headers);
    
    Proto generate(String messageComment, String fieldComment, String packageName, T dataModel, String... headers);
    
    Collection<T> extractDataClasses(Collection<T> input, String targetDirectory);

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

}

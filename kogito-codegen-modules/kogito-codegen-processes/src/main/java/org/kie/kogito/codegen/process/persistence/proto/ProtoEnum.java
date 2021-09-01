/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.codegen.process.persistence.proto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProtoEnum extends ProtoComponent {

    protected Map<String, Integer> fields = new HashMap<>();
    protected boolean sortedWithAnnotation = false;

    public ProtoEnum(String name, String javaPackageOption) {
        super(name, javaPackageOption);
    }

    public Map<String, Integer> getFields() {
        return Collections.unmodifiableMap(fields);
    }

    public void addField(String field, Integer ordinal, boolean sortedWithAnnotation) {
        if (fields.size() > 0 && this.sortedWithAnnotation && !sortedWithAnnotation) {
            throw new IllegalArgumentException("Cannot mix annotation based sorting with not annotated. Field=" + field);
        }
        this.sortedWithAnnotation = sortedWithAnnotation;
        fields.put(field, ordinal);
    }

    @Override
    public String serialize() {
        StringBuilder tostring = new StringBuilder();
        if (comment != null) {
            tostring.append("/* ").append(comment).append(" */ \n");
        }
        tostring.append("enum ").append(name).append(" { \n");
        if (javaPackageOption != null) {
            tostring.append("\toption java_package = \"").append(javaPackageOption).append("\";\n");
        }
        fields.forEach((value, ordinal) -> tostring
                .append("\t")
                .append(value)
                .append(" = ")
                .append(ordinal)
                .append(";\n"));
        tostring.append("}\n");
        return tostring.toString();
    }
}

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.util.HashMap;
import java.util.Map;

public class ProtoEnum {

    private String name;
    private String javaPackageOption;
    private Map<String, Integer> fields = new HashMap<>();
    private String comment;

    public ProtoEnum(String name, String javaPackageOption) {
        this.name = name;
        this.javaPackageOption = javaPackageOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getFields() {
        return fields;
    }

    public void setFields(Map<String, Integer> fields) {
        this.fields = fields;
    }

    public String getJavaPackageOption() {
        return javaPackageOption;
    }

    public void setJavaPackageOption(String javaPackageOption) {
        this.javaPackageOption = javaPackageOption;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String addField(String field, Integer ordinal) {
        fields.put(field, ordinal);
        return field;
    }

    @Override
    public String toString() {
        StringBuilder tostring = new StringBuilder();
        if (comment != null) {
            tostring.append("/* " + comment + " */ \n");
        }
        tostring.append("enum " + name + " { \n");
        if (javaPackageOption != null) {
            tostring.append("\toption java_package = \"" + javaPackageOption + "\";\n");
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProtoEnum other = (ProtoEnum) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }
}

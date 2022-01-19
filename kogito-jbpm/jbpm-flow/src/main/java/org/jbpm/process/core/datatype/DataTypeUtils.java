/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.process.core.datatype;

import java.util.HashSet;
import java.util.Set;

public class DataTypeUtils {
    private static final Set<String> langClasses = new HashSet<>();

    static {
        langClasses.add("Integer");
        langClasses.add("Boolean");
        langClasses.add("String");
        langClasses.add("Float");
        langClasses.add("Object");
    }

    public static String ensureLangPrefix(String type) {
        return langClasses.contains(type) ? "java.lang." + type : type;
    }

    public static boolean isAssignableFrom(DataType target, DataType src) {
        if (target == src || target.equals(src)) {
            return true;
        }
        try {
            return target.getObjectClass().isAssignableFrom(src.getObjectClass());
        } catch (Exception e) {
            // compare string types
        }
        return target.getStringType().equals(src.getStringType());
    }

    private DataTypeUtils() {

    }
}

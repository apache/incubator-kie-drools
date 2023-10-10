/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.codegen.execmodel.util;

import com.github.javaparser.ast.type.Type;
import org.kie.internal.ruleunit.RuleUnitDescription;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static org.drools.model.codegen.execmodel.generator.DrlxParseUtil.toClassOrInterfaceType;

public class RuleCodegenUtils {

    private RuleCodegenUtils() {
        // utility class
    }

    public static void setGeneric(Type type, RuleUnitDescription ruleUnit) {
        type.asClassOrInterfaceType().setTypeArguments(toClassOrInterfaceType(ruleUnit.getCanonicalName()));
    }

    public static void setGeneric(Type type, String typeArgument) {
        type.asClassOrInterfaceType().setTypeArguments(parseClassOrInterfaceType(toNonPrimitiveType(typeArgument)));
    }

    public static String toNonPrimitiveType(String type) {
        switch (type) {
            case "int":
                return "Integer";
            case "long":
                return "Long";
            case "double":
                return "Double";
            case "float":
                return "Float";
            case "short":
                return "Short";
            case "byte":
                return "Byte";
            case "char":
                return "Character";
            case "boolean":
                return "Boolean";
            default:
                return type;
        }
    }

    public static String toCamelCase(String inputString) {
        return Stream.of(inputString.split(" "))
                .map(s -> s.length() > 1 ? s.substring(0, 1).toUpperCase() + s.substring(1) : s.substring(0, 1).toUpperCase())
                .collect(Collectors.joining());
    }

    public static String toKebabCase(String inputString) {
        return inputString.replaceAll("(.)(\\p{Upper})", "$1-$2").toLowerCase();
    }
}

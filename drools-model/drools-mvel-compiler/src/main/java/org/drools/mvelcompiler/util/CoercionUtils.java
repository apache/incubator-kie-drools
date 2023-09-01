/**
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
package org.drools.mvelcompiler.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

public class CoercionUtils {

    public static final String PUT_CALL = "put";

    public static Expression coerceMapValueToString(Optional<Type> type, Expression originalValue) {
        return type.flatMap(t -> {
            if (t instanceof ParameterizedType) {
                ParameterizedType parametrizedType = (ParameterizedType) t;
                if (parametrizedType.getActualTypeArguments().length == 2) {
                    Type valueType = parametrizedType.getActualTypeArguments()[1];

                    if (valueType.equals(String.class)) {
                        return Optional.of((Expression)new MethodCallExpr(new NameExpr("java.lang.String"),
                                                                          "valueOf",
                                                                          NodeList.nodeList(originalValue)));
                    }
                }
            }
            return Optional.empty();
        }).orElse(originalValue);
    }

}

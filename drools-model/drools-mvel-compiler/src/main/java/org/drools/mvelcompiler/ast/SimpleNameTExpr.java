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
package org.drools.mvelcompiler.ast;

import java.lang.reflect.Type;
import java.util.Optional;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;

public class SimpleNameTExpr implements TypedExpression {

    private final String constraintName;
    private final Class<?> clazz;

    public SimpleNameTExpr(String constraintName, Class<?> clazz) {
        this.constraintName = constraintName;
        this.clazz = clazz;
    }

    @Override
    public Optional<Type> getType() {
        return Optional.ofNullable(clazz);
    }

    @Override
    public Expression toJavaExpression() {
        return new NameExpr(constraintName);
    }

    @Override
    public String toString() {
        return "SimpleNameTExpr{" +
                "name=" + constraintName +
                ",clazz=" + clazz +
                '}';
    }
}

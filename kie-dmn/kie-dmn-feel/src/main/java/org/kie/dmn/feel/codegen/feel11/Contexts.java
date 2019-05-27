/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package org.kie.dmn.feel.codegen.feel11;

import java.lang.reflect.Method;
import java.util.Map;

import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.StaticJavaParser.parseType;

public class Contexts {

    public static final Type MapT = parseType(Map.class.getCanonicalName());

    public static Expression getKey(Expression currentContext, CompositeType contextType, String key) {
        if (contextType instanceof MapBackedType) {
            EnclosedExpr enclosedExpr = Expressions.castTo(MapT, currentContext);
            return new MethodCallExpr(enclosedExpr, "get")
                    .addArgument(new StringLiteralExpr(key));
        } else if (contextType instanceof JavaBackedType) {
            JavaBackedType javaBackedType = (JavaBackedType) contextType;
            Class<?> wrappedType = javaBackedType.getWrapped();
            Method accessor = EvalHelper.getGenericAccessor(wrappedType, key);
            Type type = parseType(wrappedType.getCanonicalName());
            return new MethodCallExpr(Expressions.castTo(type, currentContext), accessor.getName());
        } else {
            throw new UnsupportedOperationException("A Composite type is either MapBacked or JavaBAcked");
        }
    }
}

/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.feel.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;

import static com.github.javaparser.StaticJavaParser.parseExpression;
import static com.github.javaparser.utils.StringEscapeUtils.escapeJava;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ASLIST_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DURATION_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.DURATION_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.FEEL_TIME_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.INTEGER_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_DATE_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.LOCAL_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OFFSETTIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OFFSETTIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.OF_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.PARSE_S;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.STRING_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.TEMPORALACCESSOR_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONED_DATE_TIME_CT;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONED_DATE_TIME_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONE_ID_N;
import static org.kie.dmn.feel.codegen.feel11.CodegenConstants.ZONE_OFFSET_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.COMPARABLEPERIOD_CT;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.COMPARABLEPERIOD_N;
import static org.kie.dmn.feel.codegen.feel11.DMNCodegenConstants.TIMEFUNCTION_N;

/**
 * Class meant to provide commons utility methods for codegen.
 * This class should not have any reference to dmn-specific classes/methods.
 */
public class CodegenUtils {

    private CodegenUtils() {
    }

    public static Expression getEnumExpression(Enum enumType) {
        Expression scopeExpression = parseExpression(enumType.getClass().getCanonicalName());
        return new FieldAccessExpr(scopeExpression, enumType.name());
    }

    public static VariableDeclarationExpr getObjectExpression(Object object, String variableName) {
        if (object instanceof ComparablePeriod comparablePeriod) {
            NodeList arguments = NodeList.nodeList(new StringLiteralExpr(comparablePeriod.asPeriod().toString()));

            return getVariableDeclaratorWithMethodCall(variableName,
                                                       COMPARABLEPERIOD_CT,
                                                       PARSE_S,
                                                       COMPARABLEPERIOD_N, arguments);
        } else if (object instanceof Duration duration) {
            NodeList arguments = NodeList.nodeList(new StringLiteralExpr(duration.toString()));
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       DURATION_CT,
                                                       PARSE_S,
                                                       DURATION_N,
                                                       arguments);
        } else if (object instanceof LocalDate localDate) {
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localDate.getYear()),
                                                   new IntegerLiteralExpr(localDate.getMonthValue()),
                                                   new IntegerLiteralExpr(localDate.getDayOfMonth()));

            return getVariableDeclaratorWithMethodCall(variableName,
                                                       LOCAL_DATE_CT,
                                                       OF_S,
                                                       LOCAL_DATE_N,
                                                       arguments);
        } else if (object instanceof LocalDateTime localDateTime) {
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localDateTime.getYear()),
                                                   new IntegerLiteralExpr(localDateTime.getMonthValue()),
                                                   new IntegerLiteralExpr(localDateTime.getDayOfMonth()),
                                                   new IntegerLiteralExpr(localDateTime.getHour()),
                                                   new IntegerLiteralExpr(localDateTime.getMinute()),
                                                   new IntegerLiteralExpr(localDateTime.getSecond()));
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       LOCAL_DATE_TIME_CT,
                                                       OF_S,
                                                       LOCAL_DATE_TIME_N,
                                                       arguments);
        } else if (object instanceof LocalTime localTime) {
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(localTime.getHour()),
                                                   new IntegerLiteralExpr(localTime.getMinute()),
                                                   new IntegerLiteralExpr(localTime.getSecond()),
                                                   new IntegerLiteralExpr(localTime.getNano()));
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       LOCAL_TIME_CT,
                                                       OF_S,
                                                       LOCAL_TIME_N,
                                                       arguments);
        } else if (object instanceof Number number) {
            return getVariableDeclaratorWithInitializerExpression(variableName, INTEGER_CT,
                                                                  new IntegerLiteralExpr(number.toString()));
        } else if (object instanceof OffsetTime offsetTime) {
            Expression zoneOffsetExpression = new MethodCallExpr(ZONE_OFFSET_N,
                                                                 OF_S,
                                                                 NodeList.nodeList(new StringLiteralExpr(offsetTime.getOffset().getId())));
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(offsetTime.getHour()),
                                                   new IntegerLiteralExpr(offsetTime.getMinute()),
                                                   new IntegerLiteralExpr(offsetTime.getSecond()),
                                                   new IntegerLiteralExpr(offsetTime.getNano()),
                                                   zoneOffsetExpression);
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       OFFSETTIME_CT,
                                                       OF_S,
                                                       OFFSETTIME_N,
                                                       arguments);
        } else if (object instanceof String string) {
            return getVariableDeclaratorWithInitializerExpression(variableName, STRING_CT,
                                                                  new StringLiteralExpr(escapeJava(string)));
        } else if (object instanceof ZonedDateTime zonedDateTime) {
            Expression zoneIdExpression = new MethodCallExpr(ZONE_ID_N, OF_S,
                                                             NodeList.nodeList(new StringLiteralExpr(zonedDateTime.getZone().getId())));
            NodeList arguments = NodeList.nodeList(new IntegerLiteralExpr(zonedDateTime.getYear()),
                                                   new IntegerLiteralExpr(zonedDateTime.getMonthValue()),
                                                   new IntegerLiteralExpr(zonedDateTime.getDayOfMonth()),
                                                   new IntegerLiteralExpr(zonedDateTime.getHour()),
                                                   new IntegerLiteralExpr(zonedDateTime.getMinute()),
                                                   new IntegerLiteralExpr(zonedDateTime.getSecond()),
                                                   new IntegerLiteralExpr(zonedDateTime.getNano()),
                                                   zoneIdExpression);
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       ZONED_DATE_TIME_CT,
                                                       OF_S,
                                                       ZONED_DATE_TIME_N,
                                                       arguments);
        } else if (object instanceof TemporalAccessor temporalAccessor) {
            // FallBack in case of Parse or other unmanaged classes - keep at the end
            String parsedString = DateTimeEvalHelper.toParsableString(temporalAccessor);
            Expression feelTimeExpression = new FieldAccessExpr(TIMEFUNCTION_N, FEEL_TIME_S);
            return getVariableDeclaratorWithMethodCall(variableName,
                                                       TEMPORALACCESSOR_CT,
                                                       PARSE_S,
                                                       feelTimeExpression,
                                                       NodeList.nodeList(new StringLiteralExpr(parsedString)));
        } else {
            throw new UnsupportedOperationException("Unexpected Object: " + object + " " + object.getClass());
        }
    }

    public static StringLiteralExpr getStringLiteralExpr(String text) {
        if (text.startsWith("\"") && text.endsWith("\"")) {
            String actualStringContent = text.substring(1, text.length() - 1); // remove start/end " from the FEEL text expression.
            String unescaped = StringEvalHelper.unescapeString(actualStringContent); // unescapes String, FEEL-style
            return new StringLiteralExpr().setString(unescaped); // setString escapes the contents Java-style
        } else {
            return new StringLiteralExpr().setString(text);
        }
    }

    public static Expression getListExpression(List<Expression> expressions) {
        ExpressionStmt asListExpression = new ExpressionStmt();
        MethodCallExpr arraysCallExpression = new MethodCallExpr();
        SimpleName arraysName = new SimpleName(Arrays.class.getName());
        arraysCallExpression.setScope(new NameExpr(arraysName));
        arraysCallExpression.setName(new SimpleName(ASLIST_S));
        asListExpression.setExpression(arraysCallExpression);
        NodeList<Expression> arguments = new NodeList<>();
        arguments.addAll(expressions);
        arraysCallExpression.setArguments(arguments);
        asListExpression.setExpression(arraysCallExpression);
        return asListExpression.getExpression();
    }

    public static VariableDeclarationExpr getVariableDeclaratorWithObjectCreation(String variableName,
                                                                                  ClassOrInterfaceType variableType,
                                                                                  NodeList<Expression> arguments) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final ObjectCreationExpr objectCreationExpr = new ObjectCreationExpr(null, variableType, arguments);
        variableDeclarator.setInitializer(objectCreationExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    public static VariableDeclarationExpr getVariableDeclaratorWithMethodCall(String variableName,
                                                                              ClassOrInterfaceType variableType,
                                                                              String name,
                                                                              Expression scope,
                                                                              NodeList<Expression> arguments) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final MethodCallExpr methodCallExpr = new MethodCallExpr(scope, name, arguments);
        variableDeclarator.setInitializer(methodCallExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    public static VariableDeclarationExpr getVariableDeclaratorWithFieldAccessExpr(String variableName,
                                                                                   ClassOrInterfaceType variableType,
                                                                                   String name,
                                                                                   Expression scope) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        final FieldAccessExpr methodCallExpr = new FieldAccessExpr(scope, name);
        variableDeclarator.setInitializer(methodCallExpr);
        return new VariableDeclarationExpr(variableDeclarator);
    }

    public static VariableDeclarationExpr getVariableDeclaratorWithInitializerExpression(String variableName,
                                                                                         ClassOrInterfaceType variableType,
                                                                                         Expression initializer) {
        final VariableDeclarator variableDeclarator =
                new VariableDeclarator(variableType, variableName);
        variableDeclarator.setInitializer(initializer);
        return new VariableDeclarationExpr(variableDeclarator);
    }
}
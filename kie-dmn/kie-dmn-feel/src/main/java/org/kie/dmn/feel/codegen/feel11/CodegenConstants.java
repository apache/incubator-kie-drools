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
package org.kie.dmn.feel.codegen.feel11;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.kie.dmn.feel.lang.ast.ASTNode;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.BetweenNode;
import org.kie.dmn.feel.lang.ast.BooleanNode;
import org.kie.dmn.feel.lang.ast.CTypeNode;
import org.kie.dmn.feel.lang.ast.ContextEntryNode;
import org.kie.dmn.feel.lang.ast.ContextNode;
import org.kie.dmn.feel.lang.ast.ContextTypeNode;
import org.kie.dmn.feel.lang.ast.DashNode;
import org.kie.dmn.feel.lang.ast.FilterExpressionNode;
import org.kie.dmn.feel.lang.ast.ForExpressionNode;
import org.kie.dmn.feel.lang.ast.FormalParameterNode;
import org.kie.dmn.feel.lang.ast.FunctionDefNode;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.FunctionTypeNode;
import org.kie.dmn.feel.lang.ast.IfExpressionNode;
import org.kie.dmn.feel.lang.ast.InNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.InstanceOfNode;
import org.kie.dmn.feel.lang.ast.IterationContextNode;
import org.kie.dmn.feel.lang.ast.ListNode;
import org.kie.dmn.feel.lang.ast.ListTypeNode;
import org.kie.dmn.feel.lang.ast.NameDefNode;
import org.kie.dmn.feel.lang.ast.NameRefNode;
import org.kie.dmn.feel.lang.ast.NamedParameterNode;
import org.kie.dmn.feel.lang.ast.NullNode;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.lang.ast.PathExpressionNode;
import org.kie.dmn.feel.lang.ast.QualifiedNameNode;
import org.kie.dmn.feel.lang.ast.QuantifiedExpressionNode;
import org.kie.dmn.feel.lang.ast.RangeNode;
import org.kie.dmn.feel.lang.ast.SignedUnaryNode;
import org.kie.dmn.feel.lang.ast.StringNode;
import org.kie.dmn.feel.lang.ast.TemporalConstantNode;
import org.kie.dmn.feel.lang.ast.UnaryTestListNode;
import org.kie.dmn.feel.lang.ast.UnaryTestNode;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.impl.ComparablePeriod;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.functions.TimeFunction;
import org.kie.dmn.feel.util.NumberEvalHelper;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;
import static com.github.javaparser.StaticJavaParser.parseType;

/**
 * Class used to store constant values needed for codegen
 */
public class CodegenConstants {


    // String
    public static final String ADDFIELD_S = "addField";
    public static final String ASLIST_S = "asList";
    public static final String COMPILEDFEELSUPPORT_S = "CompiledFEELSupport";
    public static final String DETERMINEOPERATOR_S = "determineOperator";
    public static final String EVALUATE_S = "evaluate";
    public static final String FEELCTX_S = "feelExprCtx";
    public static final String FEEL_TIME_S = "FEEL_TIME";
    public static final String GETBIGDECIMALORNULL_S  = "getBigDecimalOrNull";
    public static final String GETCOMPILEDUNARYTESTS_S = "getCompiledUnaryTests";
    public static final String INSTANCE_S = "INSTANCE";
    public static final String NOTIFYCOMPILATIONERROR_S = "notifyCompilationError";
    public static final String OF_S = "of";
    public static final String PARSE_S = "parse";
    public static final String PUT_S = "put";
    public static final String RANGEBOUNDARY_S = Range.RangeBoundary.class.getCanonicalName();
    public static final String VALUEOF_S = "valueOf";
    public static final String VAR_S = "var";

    // NameExpr
    // java types
    public static final NameExpr ARRAYS_N = new NameExpr(Arrays.class.getCanonicalName());
    public static final NameExpr BIG_DECIMAL_N = new NameExpr(BigDecimal.class.getCanonicalName());
    public static final NameExpr DURATION_N = new NameExpr(Duration.class.getCanonicalName());
    public static final NameExpr LOCAL_DATE_N = new NameExpr(LocalDate.class.getCanonicalName());
    public static final NameExpr LOCAL_DATE_TIME_N = new NameExpr(LocalDateTime.class.getCanonicalName());
    public static final NameExpr LOCAL_TIME_N = new NameExpr(LocalTime.class.getCanonicalName());
    public static final NameExpr OFFSETTIME_N = new NameExpr(OffsetTime.class.getCanonicalName());
    public static final NameExpr TEMPORALACCESSOR_N = new NameExpr(TemporalAccessor.class.getCanonicalName());
    public static final NameExpr ZONED_DATE_TIME_N = new NameExpr(ZonedDateTime.class.getCanonicalName());
    public static final NameExpr ZONE_ID_N = new NameExpr(ZoneId.class.getCanonicalName());
    public static final NameExpr ZONE_OFFSET_N = new NameExpr(ZoneOffset.class.getCanonicalName());

    // DMN
    public static final NameExpr INFIXOPERATOR_N = new NameExpr(InfixOperator.class.getCanonicalName());
    public static final NameExpr COMPILEDFEELSUPPORT_N = new NameExpr(COMPILEDFEELSUPPORT_S);
    public static final NameExpr COMPARABLEPERIOD_N = new NameExpr(ComparablePeriod.class.getCanonicalName());
    public static final NameExpr FEELCTX_N = new NameExpr(FEELCTX_S);
    public static final NameExpr JAVABACKEDTYPE_N = new NameExpr(JavaBackedType.class.getCanonicalName());
    public static final NameExpr NUMBEREVALHELPER_N = new NameExpr(NumberEvalHelper.class.getCanonicalName());
    public static final NameExpr TIMEFUNCTION_N = new NameExpr(TimeFunction.class.getCanonicalName());

    // Type
    public static final Type LIST_T = parseType(List.class.getCanonicalName());

    // ClassOrInterfaceType
    // java types
    public static final ClassOrInterfaceType BIGDECIMAL_CT = parseClassOrInterfaceType(BigDecimal.class.getCanonicalName());
    public static final ClassOrInterfaceType BOOLEAN_CT = parseClassOrInterfaceType(Boolean.class.getCanonicalName());
    public static final ClassOrInterfaceType DURATION_CT = parseClassOrInterfaceType(Duration.class.getCanonicalName());
    public static final ClassOrInterfaceType HASHMAP_CT = parseClassOrInterfaceType(HashMap.class.getCanonicalName());
    public static final ClassOrInterfaceType FUNCTION_CT = parseClassOrInterfaceType("java.util.function.Function<EvaluationContext, Object>");
    public static final ClassOrInterfaceType LOCAL_DATE_CT = parseClassOrInterfaceType(LocalDate.class.getCanonicalName());
    public static final ClassOrInterfaceType LOCAL_DATE_TIME_CT = parseClassOrInterfaceType(LocalDateTime.class.getCanonicalName());
    public static final ClassOrInterfaceType LOCAL_TIME_CT = parseClassOrInterfaceType(LocalTime.class.getCanonicalName());
    public static final ClassOrInterfaceType OFFSETTIME_CT = parseClassOrInterfaceType(OffsetTime.class.getCanonicalName());
    public static final ClassOrInterfaceType MAP_CT = parseClassOrInterfaceType(Map.class.getCanonicalName());
    public static final ClassOrInterfaceType OBJECT_CT = parseClassOrInterfaceType(Object.class.getCanonicalName());
    public static final ClassOrInterfaceType STRING_CT = parseClassOrInterfaceType(String.class.getCanonicalName());
    public static final ClassOrInterfaceType TEMPORALACCESSOR_CT = parseClassOrInterfaceType(TemporalAccessor.class.getCanonicalName());
    public static final ClassOrInterfaceType ZONED_DATE_TIME_CT = parseClassOrInterfaceType(ZonedDateTime.class.getCanonicalName());

    // DMN
    public static final ClassOrInterfaceType COMPARABLEPERIOD_CT = parseClassOrInterfaceType(ComparablePeriod.class.getCanonicalName());
    public static final ClassOrInterfaceType COMPILEDFEELSUPPORT_CT = parseClassOrInterfaceType(CompiledFEELSupport.class.getSimpleName());
    public static final ClassOrInterfaceType FEELFUNCTION_CT = parseClassOrInterfaceType(FEELFunction.class.getCanonicalName());
    public static final ClassOrInterfaceType INFIXOPERATOR_CT = parseClassOrInterfaceType(InfixOperator.class.getCanonicalName());
    public static final ClassOrInterfaceType JAVABACKEDTYPE_CT = parseClassOrInterfaceType(JavaBackedType.class.getCanonicalName());
    public static final ClassOrInterfaceType MAPBACKEDTYPE_CT = parseClassOrInterfaceType(MapBackedType.class.getCanonicalName());
    public static final ClassOrInterfaceType PARAM_CT = parseClassOrInterfaceType(FEELFunction.Param.class.getCanonicalName());
    public static final ClassOrInterfaceType TYPE_CT = parseClassOrInterfaceType(org.kie.dmn.feel.lang.Type.class.getCanonicalName());
    public static final ClassOrInterfaceType UNARYTEST_CT = parseClassOrInterfaceType(UnaryTest.class.getCanonicalName());

    // BaseNode
    public static final ClassOrInterfaceType ASTNODE_CT = parseClassOrInterfaceType(ASTNode.class.getCanonicalName());
    public static final ClassOrInterfaceType ATLITERALNODE_CT = parseClassOrInterfaceType(AtLiteralNode.class.getCanonicalName());
    public static final ClassOrInterfaceType BETWEENNODE_CT = parseClassOrInterfaceType(BetweenNode.class.getCanonicalName());
    public static final ClassOrInterfaceType BOOLEANNODE_CT = parseClassOrInterfaceType(BooleanNode.class.getCanonicalName());
    public static final ClassOrInterfaceType CONTEXTNODE_CT = parseClassOrInterfaceType(ContextNode.class.getCanonicalName());
    public static final ClassOrInterfaceType CONTEXTENTRYNODE_CT = parseClassOrInterfaceType(ContextEntryNode.class.getCanonicalName());
    public static final ClassOrInterfaceType CONTEXTTYPENODE_CT = parseClassOrInterfaceType(ContextTypeNode.class.getCanonicalName());
    public static final ClassOrInterfaceType CTYPENODE_CT = parseClassOrInterfaceType(CTypeNode.class.getCanonicalName());
    public static final ClassOrInterfaceType DASHNODE_CT = parseClassOrInterfaceType(DashNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FILTEREXPRESSIONNODE_CT = parseClassOrInterfaceType(FilterExpressionNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FOREXPRESSIONNODE_CT = parseClassOrInterfaceType(ForExpressionNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FORMALPARAMETERNODE_CT = parseClassOrInterfaceType(FormalParameterNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FUNCTIONDEFNODE_CT = parseClassOrInterfaceType(FunctionDefNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FUNCTIONTYPENODE_CT = parseClassOrInterfaceType(FunctionTypeNode.class.getCanonicalName());
    public static final ClassOrInterfaceType FUNCTIONINVOCATIONNODE_CT = parseClassOrInterfaceType(FunctionInvocationNode.class.getCanonicalName());
    public static final ClassOrInterfaceType IFEXPRESSIONNODE_CT = parseClassOrInterfaceType(IfExpressionNode.class.getCanonicalName());
    public static final ClassOrInterfaceType INFIXOPNODE_CT = parseClassOrInterfaceType(InfixOpNode.class.getCanonicalName());
    public static final ClassOrInterfaceType INNODE_CT = parseClassOrInterfaceType(InNode.class.getCanonicalName());
    public static final ClassOrInterfaceType INSTANCEOFNODE_CT = parseClassOrInterfaceType(InstanceOfNode.class.getCanonicalName());
    public static final ClassOrInterfaceType ITERATIONCONTEXTNODE_CT = parseClassOrInterfaceType(IterationContextNode.class.getCanonicalName());
    public static final ClassOrInterfaceType LISTNODE_CT = parseClassOrInterfaceType(ListNode.class.getCanonicalName());
    public static final ClassOrInterfaceType LISTTYPENODE_CT = parseClassOrInterfaceType(ListTypeNode.class.getCanonicalName());
    public static final ClassOrInterfaceType NAMEDEFNODE_CT = parseClassOrInterfaceType(NameDefNode.class.getCanonicalName());
    public static final ClassOrInterfaceType NAMEDPARAMETERNODE_CT = parseClassOrInterfaceType(NamedParameterNode.class.getCanonicalName());
    public static final ClassOrInterfaceType NAMEREFNODE_CT = parseClassOrInterfaceType(NameRefNode.class.getCanonicalName());
    public static final ClassOrInterfaceType NULLNODE_CT = parseClassOrInterfaceType(NullNode.class.getCanonicalName());
    public static final ClassOrInterfaceType NUMBERNODE_CT = parseClassOrInterfaceType(NumberNode.class.getCanonicalName());
    public static final ClassOrInterfaceType PATHEXPRESSIONNODE_CT = parseClassOrInterfaceType(PathExpressionNode.class.getCanonicalName());
    public static final ClassOrInterfaceType QUALIFIEDNAMENODE_CT = parseClassOrInterfaceType(QualifiedNameNode.class.getCanonicalName());
    public static final ClassOrInterfaceType QUANTIFIEDEXPRESSIONNODE_CT = parseClassOrInterfaceType(QuantifiedExpressionNode.class.getCanonicalName());
    public static final ClassOrInterfaceType RANGENODE_CT = parseClassOrInterfaceType(RangeNode.class.getCanonicalName());
    public static final ClassOrInterfaceType SIGNEDUNARYNODE_CT = parseClassOrInterfaceType(SignedUnaryNode.class.getCanonicalName());
    public static final ClassOrInterfaceType STRINGNODE_CT = parseClassOrInterfaceType(StringNode.class.getCanonicalName());
    public static final ClassOrInterfaceType TEMPORALCONSTANTNODE_CT = parseClassOrInterfaceType(TemporalConstantNode.class.getCanonicalName());
    public static final ClassOrInterfaceType UNARYTESTLISTNODE_CT = parseClassOrInterfaceType(UnaryTestListNode.class.getCanonicalName());
    public static final ClassOrInterfaceType UNARYTESTNODE_CT = parseClassOrInterfaceType(UnaryTestNode.class.getCanonicalName());

    private CodegenConstants() {
    }
}

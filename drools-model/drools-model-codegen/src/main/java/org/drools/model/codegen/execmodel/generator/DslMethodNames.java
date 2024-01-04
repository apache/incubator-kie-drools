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
package org.drools.model.codegen.execmodel.generator;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import org.drools.model.prototype.PrototypeDSL;

public class DslMethodNames {

    private DslMethodNames() { }

    public static final NameExpr DSL_NAMESPACE = new NameExpr("D");

    // rules
    public static final String RULE_CALL = "rule";
    public static final String UNIT_CALL = "unit";
    public static final String ATTRIBUTE_CALL = "attribute";
    public static final String SUPPLY_CALL = "supply";
    public static final String METADATA_CALL = "metadata";
    public static final String BUILD_CALL = "build";
    public static final String ADD_ANNOTATION_CALL = "addAnnotation";
    public static final String ANNOTATION_VALUE_CALL = "annotationValue";

    // queries
    public static final String QUERY_INVOCATION_CALL = "call";
    public static final String QUERY_CALL = "query";
    public static final String VALUE_OF_CALL = "valueOf";

    // variables
    public static final String BIND_AS_CALL = "as";
    public static final String DECLARATION_OF_CALL = "declarationOf";
    public static final String GLOBAL_OF_CALL = "globalOf";
    public static final String TYPE_META_DATA_CALL = "typeMetaData";

    // entry points
    public static final String FROM_CALL = "from";
    public static final String REACTIVE_FROM_CALL = "reactiveFrom";
    public static final String ENTRY_POINT_CALL = "entryPoint";
    public static final String WINDOW_CALL = "window";

    // patterns
    public static final String PATTERN_CALL = "pattern";
    public static final String WATCH_CALL = "watch";
    public static final String PASSIVE_CALL = "passive";
    public static final String NOT_CALL = "not";
    public static final String EXISTS_CALL = "exists";
    public static final String FORALL_CALL = "forall";
    public static final String ACCUMULATE_CALL = "accumulate";
    public static final String GROUP_BY_CALL = "groupBy";
    public static final String ACC_FUNCTION_CALL = "accFunction";
    public static final String ACC_WITH_EXTERNAL_DECLRS_CALL = "with";
    public static final String EVAL_CALL = "eval";
    public static final String NO_OP_EXPR = "NO_OP_EXPR";

    // prototypes
    public static final NameExpr PROTO_DSL_NAMESPACE = new NameExpr(PrototypeDSL.class.getCanonicalName());
    public static final String PROTO_PATTERN_CALL = "protoPattern";
    public static final String PROTO_EXPR_CALL = "expr";
    public static final String PROTOTYPE_FACT_CALL = "prototypeFact";
    public static final String PROTOTYPE_VARIABLE_CALL = "variable";

    // expressions
    public static final String EVAL_EXPR_CALL = "expr";
    public static final String EXPR_CALL = "expr";
    public static final String REACT_ON_CALL = "reactOn";
    public static final String BIND_CALL = "bind";
    public static final String EXPR_OR_CALL = "or";
    public static final String EXPR_AND_CALL = "and";
    public static final String EXPR_END_OR_CALL = "endOr";
    public static final String EXPR_END_AND_CALL = "endAnd";

    // indexing
    public static final String ALPHA_INDEXED_BY_CALL = "alphaIndexedBy";
    public static final String BETA_INDEXED_BY_CALL = "betaIndexedBy";

    // consequences
    public static final String EXECUTE_CALL = "execute";
    public static final String ON_CALL = "on";
    public static final String UNIT_DATA_CALL = "unitData";
    public static final String WHEN_CALL = "when";
    public static final String ELSE_WHEN_CALL = "elseWhen";
    public static final String THEN_CALL = "then";
    public static final String BREAKING_CALL = "breaking";
    public static final String GET_CHANNEL_CALL = "getChannel";

    // and/or
    public static final String AND_CALL = "and";
    public static final String OR_CALL = "or";

    public static MethodCallExpr createDslTopLevelMethod(String name) {
        return new MethodCallExpr(DSL_NAMESPACE.clone(), name);
    }

    public static MethodCallExpr createDslTopLevelMethod(String name, NodeList<Expression> arguments) {
        return new MethodCallExpr(DSL_NAMESPACE.clone(), name, arguments);
    }

    public static boolean isDslTopLevelNamespace(Expression expr) {
        return DSL_NAMESPACE.equals(expr);
    }

    public static MethodCallExpr createProtoDslTopLevelMethod(String name) {
        return new MethodCallExpr(PROTO_DSL_NAMESPACE.clone(), name);
    }
}

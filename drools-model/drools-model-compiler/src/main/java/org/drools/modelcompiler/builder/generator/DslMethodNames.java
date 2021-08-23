/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.generator;

public class DslMethodNames {

    private DslMethodNames() { }

    // rules
    public static final String RULE_CALL = "D.rule";
    public static final String UNIT_CALL = "unit";
    public static final String ATTRIBUTE_CALL = "attribute";
    public static final String SUPPLY_CALL = "D.supply";
    public static final String METADATA_CALL = "metadata";
    public static final String BUILD_CALL = "build";
    public static final String ADD_ANNOTATION_CALL = "addAnnotation";
    public static final String ANNOTATION_VALUE_CALL = "D.annotationValue";

    // queries
    public static final String QUERY_INVOCATION_CALL = "call";
    public static final String QUERY_CALL = "D.query";
    public static final String VALUE_OF_CALL = "D.valueOf";

    // variables
    public static final String BIND_AS_CALL = "as";
    public static final String DECLARATION_OF_CALL = "D.declarationOf";
    public static final String GLOBAL_OF_CALL = "D.globalOf";
    public static final String TYPE_META_DATA_CALL = "D.typeMetaData";

    // entry points
    public static final String FROM_CALL = "D.from";
    public static final String REACTIVE_FROM_CALL = "D.reactiveFrom";
    public static final String ENTRY_POINT_CALL = "D.entryPoint";
    public static final String WINDOW_CALL = "D.window";

    // patterns
    public static final String PATTERN_CALL = "D.pattern";
    public static final String WATCH_CALL = "watch";
    public static final String PASSIVE_CALL = "passive";
    public static final String NOT_CALL = "D.not";
    public static final String EXISTS_CALL = "D.exists";
    public static final String FORALL_CALL = "D.forall";
    public static final String ACCUMULATE_CALL = "D.accumulate";
    public static final String ACC_FUNCTION_CALL = "D.accFunction";
    public static final String ACC_WITH_EXTERNAL_DECLRS_CALL = "with";
    public static final String EVAL_CALL = "D.eval";
    public static final String NO_OP_EXPR = "NO_OP_EXPR";

    // expressions
    public static final String EVAL_EXPR_CALL = "D.expr";
    public static final String EXPR_CALL = "expr";
    public static final String REACT_ON_CALL = "D.reactOn";
    public static final String BIND_CALL = "bind";
    public static final String EXPR_OR_CALL = "or";
    public static final String EXPR_AND_CALL = "and";
    public static final String EXPR_END_OR_CALL = "endOr";
    public static final String EXPR_END_AND_CALL = "endAnd";

    // indexing
    public static final String ALPHA_INDEXED_BY_CALL = "D.alphaIndexedBy";
    public static final String BETA_INDEXED_BY_CALL = "D.betaIndexedBy";

    // consequences
    public static final String EXECUTE_CALL = "execute";
    public static final String ON_CALL = "D.on";
    public static final String UNIT_DATA_CALL = "D.unitData";
    public static final String WHEN_CALL = "D.when";
    public static final String ELSE_WHEN_CALL = "elseWhen";
    public static final String THEN_CALL = "then";
    public static final String BREAKING_CALL = "breaking";
    public static final String GET_CHANNEL_CALL = "getChannel";

    // and/or
    public static final String AND_CALL = "D.and";
    public static final String OR_CALL = "D.or";
}

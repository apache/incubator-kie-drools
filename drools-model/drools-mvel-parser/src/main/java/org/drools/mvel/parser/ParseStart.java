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
package org.drools.mvel.parser;

import com.github.javaparser.ParseException;
import com.github.javaparser.Provider;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

/**
 * The start production for JavaParser.
 * Tells JavaParser what piece of Java code it can expect.
 * For example,
 * COMPILATION_UNIT indicates a complete Java file,
 * and CLASS_BODY would indicate the part of a class that is within { and }.
 *
 * @see MvelParser#parse(ParseStart, Provider)
 */
@FunctionalInterface
public interface ParseStart<R> {
    ParseStart<BlockStmt> BLOCK = GeneratedMvelParser::BlockParseStart;
    ParseStart<Expression> EXPRESSION = GeneratedMvelParser::ExpressionParseStart;
    ParseStart<ClassOrInterfaceType> CLASS_OR_INTERFACE_TYPE = GeneratedMvelParser::ClassOrInterfaceTypeParseStart;
    ParseStart<Type> TYPE = GeneratedMvelParser::ResultTypeParseStart;
    ParseStart<ExplicitConstructorInvocationStmt> EXPLICIT_CONSTRUCTOR_INVOCATION_STMT = GeneratedMvelParser::ExplicitConstructorInvocationParseStart;
    ParseStart<Name> NAME = GeneratedMvelParser::NameParseStart;
    ParseStart<SimpleName> SIMPLE_NAME = GeneratedMvelParser::SimpleNameParseStart;
    ParseStart<CompilationUnit> COMPILATION_UNIT = GeneratedMvelParser::CompilationUnit;
    ParseStart<CompilationUnit> DRLX_COMPILATION_UNIT = GeneratedMvelParser::DrlxCompilationUnit;

    R parse(GeneratedMvelParser parser) throws ParseException, org.drools.mvel.parser.ParseException;
}

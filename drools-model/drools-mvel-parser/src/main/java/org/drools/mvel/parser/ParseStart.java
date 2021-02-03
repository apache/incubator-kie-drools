/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2016 The JavaParser Team.
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * This file is part of JavaParser.
 *
 * JavaParser can be used either under the terms of
 * a) the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * b) the terms of the Apache License
 *
 * You should have received a copy of both licenses in LICENCE.LGPL and
 * LICENCE.APACHE. Please refer to those files for details.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * Modified by Red Hat, Inc.
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

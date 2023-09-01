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

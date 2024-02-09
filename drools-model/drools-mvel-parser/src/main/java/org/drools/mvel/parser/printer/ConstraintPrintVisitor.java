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
package org.drools.mvel.parser.printer;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeArguments;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.printer.DefaultPrettyPrinterVisitor;
import com.github.javaparser.printer.configuration.ConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.PrinterConfiguration;
import org.drools.mvel.parser.ast.expr.BigDecimalLiteralExpr;
import org.drools.mvel.parser.ast.expr.BigIntegerLiteralExpr;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.expr.DrlxExpression;
import org.drools.mvel.parser.ast.expr.FullyQualifiedInlineCastExpr;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.HalfPointFreeExpr;
import org.drools.mvel.parser.ast.expr.InlineCastExpr;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.ListCreationLiteralExpressionElement;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpression;
import org.drools.mvel.parser.ast.expr.MapCreationLiteralExpressionKeyValuePair;
import org.drools.mvel.parser.ast.expr.ModifyStatement;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;
import org.drools.mvel.parser.ast.expr.OOPathChunk;
import org.drools.mvel.parser.ast.expr.OOPathExpr;
import org.drools.mvel.parser.ast.expr.PointFreeExpr;
import org.drools.mvel.parser.ast.expr.RuleBody;
import org.drools.mvel.parser.ast.expr.RuleDeclaration;
import org.drools.mvel.parser.ast.expr.TemporalChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralChunkExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralExpr;
import org.drools.mvel.parser.ast.expr.TemporalLiteralInfiniteChunkExpr;
import org.drools.mvel.parser.ast.expr.WithStatement;
import org.drools.mvel.parser.ast.visitor.DrlVoidVisitor;

import static com.github.javaparser.utils.Utils.isNullOrEmpty;
import static org.drools.mvel.parser.printer.PrintUtil.printNode;

public class ConstraintPrintVisitor extends DefaultPrettyPrinterVisitor implements DrlVoidVisitor<Void> {

    public ConstraintPrintVisitor(PrinterConfiguration prettyPrinterConfiguration) {
        super(prettyPrinterConfiguration);
    }

    @Override
    public void visit( RuleDeclaration n, Void arg ) {
        printComment(n.getComment(), arg);

        for (AnnotationExpr ae : n.getAnnotations()) {
            ae.accept(this, arg);
            printer.print(" ");
        }

        printer.print("rule ");
        n.getName().accept(this, arg);
        printer.println(" {");
        n.getRuleBody().accept(this, arg);
        printer.println("}");
    }

    @Override
    public void visit( RuleBody ruleBody, Void arg ) {
    }

    @Override
    public void visit( InlineCastExpr inlineCastExpr, Void arg ) {
        printComment(inlineCastExpr.getComment(), arg);
        inlineCastExpr.getExpression().accept( this, arg );
        printer.print( "#" );
        inlineCastExpr.getType().accept( this, arg );
    }

    @Override
    public void visit( FullyQualifiedInlineCastExpr inlineCastExpr, Void arg ) {
        printComment(inlineCastExpr.getComment(), arg);
        inlineCastExpr.getScope().accept( this, arg );
        printer.print( "#" );
        inlineCastExpr.getName().accept( this, arg );
        if (inlineCastExpr.hasArguments()) {
            printer.print( "(" );
            inlineCastExpr.getArguments().accept( this, arg );
            printer.print( ")" );
        }
    }

    @Override
    public void visit( NullSafeFieldAccessExpr nullSafeFieldAccessExpr, Void arg ) {
        printComment(nullSafeFieldAccessExpr.getComment(), arg);
        nullSafeFieldAccessExpr.getScope().accept( this, arg );
        printer.print( "!." );
        nullSafeFieldAccessExpr.getName().accept( this, arg );
    }

    @Override
    public void visit( NullSafeMethodCallExpr nullSafeMethodCallExpr, Void arg ) {
        printComment(nullSafeMethodCallExpr.getComment(), arg);
        Optional<Expression> scopeExpression = nullSafeMethodCallExpr.getScope();
        if (scopeExpression.isPresent()) {
            scopeExpression.get().accept( this, arg );
            printer.print("!.");
        }
        printTypeArgs(nullSafeMethodCallExpr, arg);
        nullSafeMethodCallExpr.getName().accept( this, arg );
        printArguments(nullSafeMethodCallExpr.getArguments(), arg);
    }

    @Override
    public void visit( PointFreeExpr pointFreeExpr, Void arg ) {
        printComment(pointFreeExpr.getComment(), arg);
        pointFreeExpr.getLeft().accept( this, arg );
        if(pointFreeExpr.isNegated()) {
            printer.print(" not");
        }
        printer.print(" ");
        pointFreeExpr.getOperator().accept( this, arg );
        if (pointFreeExpr.getArg1() != null) {
            printer.print("[");
            pointFreeExpr.getArg1().accept( this, arg );
            if (pointFreeExpr.getArg2() != null) {
                printer.print(",");
                pointFreeExpr.getArg2().accept( this, arg );
            }
            if (pointFreeExpr.getArg3() != null) {
                printer.print(",");
                pointFreeExpr.getArg3().accept( this, arg );
            }
            if (pointFreeExpr.getArg4() != null) {
                printer.print(",");
                pointFreeExpr.getArg4().accept( this, arg );
            }
            printer.print("]");
        }
        printer.print(" ");
        NodeList<Expression> rightExprs = pointFreeExpr.getRight();
        if (rightExprs.size() == 1) {
            rightExprs.get(0).accept( this, arg );
        } else {
            printer.print("(");
            if(rightExprs.isNonEmpty()) {
                rightExprs.get(0).accept(this, arg);
            }
            for (int i = 1; i < rightExprs.size(); i++) {
                printer.print(", ");
                rightExprs.get(i).accept( this, arg );
            }
            printer.print(")");
        }
    }

    @Override
    public void visit(TemporalLiteralExpr temporalLiteralExpr, Void arg) {
        printComment(temporalLiteralExpr.getComment(), arg);
        NodeList<TemporalChunkExpr> chunks = temporalLiteralExpr.getChunks();
        for (TemporalChunkExpr c : chunks) {
            c.accept(this, arg);
        }
    }

    @Override
    public void visit(TemporalLiteralChunkExpr temporalLiteralExpr, Void arg) {
        printComment(temporalLiteralExpr.getComment(), arg);
        printer.print("" + temporalLiteralExpr.getValue());
        switch (temporalLiteralExpr.getTimeUnit()) {
            case MILLISECONDS:
                printer.print("ms");
                break;
            case SECONDS:
                printer.print("s");
                break;
            case MINUTES:
                printer.print("m");
                break;
            case HOURS:
                printer.print("h");
                break;
            case DAYS:
                printer.print("d");
                break;
        }
    }

    @Override
    public void visit(TemporalLiteralInfiniteChunkExpr temporalLiteralInfiniteChunkExpr, Void arg) {
        printer.print("*");
    }

    @Override
    public void visit( DrlxExpression expr, Void arg ) {
        if (expr.getBind() != null) {
            expr.getBind().accept( this, arg );
            printer.print( " : " );
        }
        expr.getExpr().accept(this, arg);
    }

    @Override
    public void visit(OOPathExpr oopathExpr, Void arg ) {
        printComment(oopathExpr.getComment(), arg);
        NodeList<OOPathChunk> chunks = oopathExpr.getChunks();
        for (int i = 0; i <  chunks.size(); i++) {
            final OOPathChunk chunk = chunks.get(i);
            printer.print(chunk.isSingleValue() ? "." : "/");
            chunk.accept(this, arg);
            printer.print(chunk.getField().toString());

            if (chunk.getInlineCast() != null) {
                printer.print("#");
                chunk.getInlineCast().accept( this, arg );
            }

            List<DrlxExpression> condition = chunk.getConditions();
            final Iterator<DrlxExpression> iterator = condition.iterator();
            if (!condition.isEmpty()) {
                printer.print("[");
                DrlxExpression first = iterator.next();
                first.accept(this, arg);
                while(iterator.hasNext()) {
                    printer.print(",");
                    iterator.next().accept(this, arg);
                }
                printer.print("]");
            }
        }
    }

    @Override
    public void visit(HalfBinaryExpr n, Void arg) {
        printComment(n.getComment(), arg);
        printer.print(n.getOperator().asString());
        printer.print(" ");
        n.getRight().accept(this, arg);
    }

    @Override
    public void visit(HalfPointFreeExpr pointFreeExpr, Void arg ) {
        printComment(pointFreeExpr.getComment(), arg);
        if(pointFreeExpr.isNegated()) {
            printer.print("not ");
        }
        pointFreeExpr.getOperator().accept( this, arg );
        if (pointFreeExpr.getArg1() != null) {
            printer.print("[");
            pointFreeExpr.getArg1().accept( this, arg );
            if (pointFreeExpr.getArg2() != null) {
                printer.print(",");
                pointFreeExpr.getArg2().accept( this, arg );
            }
            if (pointFreeExpr.getArg3() != null) {
                printer.print(",");
                pointFreeExpr.getArg3().accept( this, arg );
            }
            if (pointFreeExpr.getArg4() != null) {
                printer.print(",");
                pointFreeExpr.getArg4().accept( this, arg );
            }
            printer.print("]");
        }
        printer.print(" ");
        NodeList<Expression> rightExprs = pointFreeExpr.getRight();
        if (rightExprs.size() == 1) {
            rightExprs.get(0).accept( this, arg );
        } else {
            printer.print("(");
            rightExprs.get(0).accept( this, arg );
            for (int i = 1; i < rightExprs.size(); i++) {
                printer.print(", ");
                rightExprs.get(i).accept( this, arg );
            }
            printer.print(")");
        }
    }

    @Override
    public void visit(BigDecimalLiteralExpr bigDecimalLiteralExpr, Void arg) {
        printer.print(bigDecimalLiteralExpr.asBigDecimal().toString());
        printer.print("B");
    }

    @Override
    public void visit(BigIntegerLiteralExpr bigIntegerLiteralExpr, Void arg) {
        printer.print(bigIntegerLiteralExpr.asBigInteger().toString());
        printer.print("I");
    }

    @Override
    public void visit(ModifyStatement modifyExpression, Void arg) {
        printer.print("modify (");
        modifyExpression.getModifyObject().accept(this, arg);
        printer.print(") { ");

        String expressionWithComma = modifyExpression.getExpressions()
                .stream()
                .filter(Objects::nonNull)
                .filter(Statement::isExpressionStmt)
                .map(n -> printNode(n.asExpressionStmt().getExpression()))
                .collect(Collectors.joining(", "));

        printer.print(expressionWithComma);
        printer.print(" }");

        printer.print(";");
    }

    @Override
    public void visit(WithStatement withExpression, Void arg) {
        printer.print("with (");
        withExpression.getWithObject().accept(this, arg);
        printer.print(") { ");

        String expressionWithComma = withExpression.getExpressions()
                .stream()
                .filter(Objects::nonNull)
                .filter(Statement::isExpressionStmt)
                .map(n -> printNode(n.asExpressionStmt().getExpression()))
                .collect(Collectors.joining(", "));

        printer.print(expressionWithComma);
        printer.print(" }");

        printer.print(";");
    }

    public void printComment(final Optional<Comment> comment, final Void arg) {
        comment.ifPresent(c -> c.accept(this, arg));
    }


    public void printTypeArgs(final NodeWithTypeArguments<?> nodeWithTypeArguments, final Void arg) {
        NodeList<Type> typeArguments = nodeWithTypeArguments.getTypeArguments().orElse(null);
        if (!isNullOrEmpty(typeArguments)) {
            printer.print("<");
            for (final Iterator<Type> i = typeArguments.iterator(); i.hasNext(); ) {
                final Type t = i.next();
                t.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(", ");
                }
            }
            printer.print(">");
        }
    }


    public void printArguments(final NodeList<Expression> args, final Void arg) {
        printer.print("(");
        if (!isNullOrEmpty(args)) {
            boolean columnAlignParameters = (args.size() > 1) &&
                    configuration.get(new DefaultConfigurationOption(DefaultPrinterConfiguration.ConfigOption.COLUMN_ALIGN_PARAMETERS))
                                    .map(ConfigurationOption::asBoolean).orElse(false);
            if (columnAlignParameters) {
                printer.indentWithAlignTo(printer.getCursor().column);
            }
            for (final Iterator<Expression> i = args.iterator(); i.hasNext(); ) {
                final Expression e = i.next();
                e.accept(this, arg);
                if (i.hasNext()) {
                    printer.print(",");
                    if (columnAlignParameters) {
                        printer.println();
                    } else {
                        printer.print(" ");
                    }
                }
            }
            if (columnAlignParameters) {
                printer.unindent();
            }
        }
        printer.print(")");
    }

    @Override
    public void visit(DrlNameExpr n, Void arg) {
        printComment(n.getComment(), arg);
        java.util.stream.IntStream.range(0, n.getBackReferencesCount()).forEach(s -> printer.print("../"));
        n.getName().accept(this, arg);
    }

    @Override
    public void visit(MapCreationLiteralExpression n, Void arg) {
        printer.print("[");

        Iterator<Expression> expressions = n.getExpressions().iterator();
        while(expressions.hasNext()) {
            expressions.next().accept(this, arg);
            if(expressions.hasNext()) {
                printer.print(", ");
            }
        }
        printer.print("]");
    }

    @Override
    public void visit(MapCreationLiteralExpressionKeyValuePair n, Void arg) {
        n.getKey().accept(this, arg);
        printer.print(" : ");
        n.getValue().accept(this, arg);
    }

    @Override
    public void visit(ListCreationLiteralExpression n, Void arg) {
        printer.print("[");

        Iterator<Expression> expressions = n.getExpressions().iterator();
        while(expressions.hasNext()) {
            expressions.next().accept(this, arg);
            if(expressions.hasNext()) {
                printer.print(", ");
            }
        }
        printer.print("]");
    }

    @Override
    public void visit(ListCreationLiteralExpressionElement n, Void arg) {
        n.getValue().accept(this, arg);
    }
}

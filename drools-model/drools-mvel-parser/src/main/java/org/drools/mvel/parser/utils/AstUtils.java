/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.mvel.parser.utils;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.Type;
import org.drools.mvel.parser.ast.expr.HalfBinaryExpr;
import org.drools.mvel.parser.ast.expr.NullSafeFieldAccessExpr;
import org.drools.mvel.parser.ast.expr.NullSafeMethodCallExpr;

public class AstUtils {

    public static boolean hasChildOfType( Node node, Class<?> nodeType ) {
        if (nodeType.isInstance( node )) {
            return true;
        }
        for (Node child : node.getChildNodes()) {
            if (hasChildOfType( child, nodeType )) {
                return true;
            }
        }
        return false;
    }

    public static Expression parseThisExprOrHalfBinary(TokenRange tokenRange, ThisExpr thisExpr, NodeList<Expression> args ) {
        return args.size() == 1 && isHalfBinaryArg( args.get( 0 ) ) ?
                transformHalfBinaryArg( tokenRange, null, thisExpr, args.get( 0 ), false) :
                new MethodCallExpr(tokenRange, null, null, new SimpleName( "this" ), args);
    }

    public static Expression parseMethodExprOrHalfBinary( TokenRange tokenRange, SimpleName name, NodeList<Expression> args ) {
        return parseMethodExprOrHalfBinary(tokenRange, null, null, name, args, false);
    }

    public static Expression parseMethodExprOrHalfBinary(TokenRange tokenRange, Expression scope, NodeList<Type> typeArguments, SimpleName name, NodeList<Expression> args, boolean nullSafe ) {
        return args.size() == 1 && isHalfBinaryArg( args.get( 0 ) ) ?
                transformHalfBinaryArg( tokenRange, scope, new NameExpr( name ), args.get( 0 ), nullSafe) :
                (nullSafe ? new NullSafeMethodCallExpr(tokenRange, scope, typeArguments, name, args) : new MethodCallExpr(tokenRange, scope, typeArguments, name, args));
    }

    private static Expression transformHalfBinaryArg(TokenRange tokenRange, Expression scope, Expression name, Expression expr, boolean nullSafe) {
        if (expr instanceof HalfBinaryExpr) {
            Expression left = scope == null ? name  : (nullSafe ? new NullSafeFieldAccessExpr(scope, null, name.asNameExpr().getName()) : new FieldAccessExpr(scope, null, name.asNameExpr().getName()));
            return new BinaryExpr( tokenRange, left, (( HalfBinaryExpr ) expr).getRight(), (( HalfBinaryExpr ) expr).getOperator().toBinaryExprOperator() );
        }
        if (expr instanceof EnclosedExpr) {
            return transformHalfBinaryArg( tokenRange, scope, name, (( EnclosedExpr ) expr).getInner(), nullSafe );
        }
        if (expr instanceof BinaryExpr) {
            BinaryExpr binary = (BinaryExpr) expr;
            Expression rewrittenLeft = transformHalfBinaryArg( tokenRange, scope, name, binary.getLeft(), nullSafe );
            Expression rewrittenRight = binary.getRight() instanceof HalfBinaryExpr && !(binary.getLeft() instanceof EnclosedExpr) ?
                    binary.getRight() :
                    transformHalfBinaryArg( tokenRange, scope, name, binary.getRight(), nullSafe );
            rewrittenRight.setParentNode( rewrittenLeft );
            return new BinaryExpr( tokenRange, rewrittenLeft, rewrittenRight, binary.getOperator() );
        }
        throw new IllegalStateException();
    }

    private static boolean isHalfBinaryArg(Expression expr) {
        if (expr instanceof HalfBinaryExpr) {
            return true;
        }
        if (expr instanceof BinaryExpr) {
            return isHalfBinaryArg( (( BinaryExpr ) expr).getLeft() );
        }
        if (expr instanceof EnclosedExpr) {
            return isHalfBinaryArg( (( EnclosedExpr ) expr).getInner() );
        }
        return false;
    }
}

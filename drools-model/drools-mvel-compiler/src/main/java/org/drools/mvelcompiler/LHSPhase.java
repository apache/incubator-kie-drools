package org.drools.mvelcompiler;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import org.drools.mvel.parser.ast.expr.DrlNameExpr;
import org.drools.mvel.parser.ast.visitor.DrlGenericVisitor;
import org.drools.mvelcompiler.ast.AssignExprT;
import org.drools.mvelcompiler.ast.ExpressionStmtT;
import org.drools.mvelcompiler.ast.FieldToAccessorTExpr;
import org.drools.mvelcompiler.ast.ListAccessExprT;
import org.drools.mvelcompiler.ast.MapPutExprT;
import org.drools.mvelcompiler.ast.SimpleNameTExpr;
import org.drools.mvelcompiler.ast.TypedExpression;
import org.drools.mvelcompiler.ast.UnalteredTypedExpression;
import org.drools.mvelcompiler.ast.VariableDeclaratorTExpr;
import org.drools.mvelcompiler.bigdecimal.BigDecimalConversion;
import org.drools.mvelcompiler.context.Declaration;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.drools.mvelcompiler.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static org.drools.core.util.ClassUtils.getAccessor;
import static org.drools.core.util.ClassUtils.getSetter;
import static org.drools.mvel.parser.printer.PrintUtil.printConstraint;
import static org.drools.mvelcompiler.bigdecimal.BigDecimalConversion.shouldConvertPlusEqualsOperatorBigDecimal;

/**
 * This phase processes the left hand side of a MVEL target expression, if present, such as
 *
 * int a = 0
 *
 * b = 2
 *
 * It also creates a new AST with the transformation rules applied i.e.
 *
 * person.name = "Name";
 *
 * becomes
 *
 * person.setName("Name");
 */
public class LHSPhase implements DrlGenericVisitor<TypedExpression, Void> {

    private Logger logger = LoggerFactory.getLogger(LHSPhase.class);

    private final MvelCompilerContext mvelCompilerContext;
    private final Optional<TypedExpression> rhs;

    public LHSPhase(MvelCompilerContext mvelCompilerContext, Optional<TypedExpression> rhs) {
        this.mvelCompilerContext = mvelCompilerContext;
        this.rhs = rhs;
    }

    public TypedExpression invoke(Statement statement) {
        logPhase("LHS phase on: {}", statement);

        TypedExpression typedExpression = statement.accept(this, null);
        if (typedExpression == null) {
            throw new MvelCompilerException("Type check of " + printConstraint(statement) + " failed.");
        }
        logger.debug("LHS phase completed");
        return typedExpression;
    }

    @Override
    public TypedExpression visit(DrlNameExpr n, Void arg) {
        logPhase("DrlNameExpr {}", n);

        String variableName = printConstraint(n);
        Optional<Declaration> declaration = mvelCompilerContext.findDeclarations(variableName);

        return declaration.<TypedExpression>map(d -> new SimpleNameTExpr(n.getNameAsString(), d.getClazz()))
                .orElseGet(() -> {
                    mvelCompilerContext.addCreatedDeclaration(variableName, getRHSType());
                    return new VariableDeclaratorTExpr(n, variableName, getRHSType(), rhs);
                });
    }

    @Override
    public TypedExpression visit(FieldAccessExpr n, Void arg) {
        logPhase("FieldAccessExpr {}", n);

        if (parentIsExpressionStmt(n)) {
            return rhsOrError();
        }

        TypedExpression scope = n.getScope().accept(this, arg);
        n.getName().accept(this, arg);

        if(parentIsArrayAccessExpr(n)) {
            return tryParseItAsMap(n, scope)
                    .map(Optional::of)
                    .orElseGet(() -> tryParseItAsSetter(n, scope, getRHSType()))
                    .orElse(new UnalteredTypedExpression(n));
        } else {
            return tryParseItAsSetter(n, scope, getRHSType())
                    .orElse(new UnalteredTypedExpression(n));
        }
    }

    private Optional<TypedExpression> tryParseItAsMap(FieldAccessExpr n, TypedExpression scope) {
        return scope.getType().flatMap(scopeType -> {
            String getterName = printConstraint(n.getName());

            return ofNullable(getAccessor((Class<?>) scopeType, getterName))
                    .filter(t -> Map.class.isAssignableFrom(t.getReturnType()))
                    .map(accessor -> new FieldToAccessorTExpr(scope, accessor, emptyList()));
        });
    }

    private Optional<TypedExpression> tryParseItAsSetter(FieldAccessExpr n, TypedExpression scope, Class<?> setterArgumentType) {
        return scope.getType().flatMap(scopeType -> {
            String setterName = printConstraint(n.getName());
            Optional<Method> optAccessor = ofNullable(getSetter((Class<?>) scopeType, setterName, setterArgumentType));

            List<TypedExpression> arguments = rhs.map(Collections::singletonList)
                    .orElse(emptyList());

            return optAccessor.map(accessor -> new FieldToAccessorTExpr(scope, accessor, arguments));
        });
    }

    @Override
    public TypedExpression visit(MethodCallExpr n, Void arg) {
        logPhase("MethodCallExpr {}", n);

        return rhsOrError();
    }

    @Override
    public TypedExpression visit(VariableDeclarationExpr n, Void arg) {
        logPhase("VariableDeclarationExpr {}", n);

        // assuming there's always one declaration for variable
        return n.getVariables().iterator().next().accept(this, arg);
    }

    @Override
    public TypedExpression visit(VariableDeclarator n, Void arg) {
        logPhase("VariableDeclarator {}", n);

        String variableName = n.getName().asString();
        Class<?> type = getRHSorLHSType(n);

        mvelCompilerContext.addDeclaration(variableName, type);

        return new VariableDeclaratorTExpr(n, variableName, type, rhs);
    }

    @Override
    public TypedExpression visit(ExpressionStmt n, Void arg) {
        logPhase("ExpressionStmt {}", n);

        Optional<TypedExpression> expression = ofNullable(n.getExpression().accept(this, arg));
        return new ExpressionStmtT(expression.orElseGet(this::rhsOrError));
    }

    @Override
    public TypedExpression visit(AssignExpr n, Void arg) {
        logPhase("AssignExpr {}", n);

        TypedExpression target = n.getTarget().accept(this, arg);

        BigDecimalConversion bigDecimalConversion = shouldConvertPlusEqualsOperatorBigDecimal(n, rhs);
        if(bigDecimalConversion.shouldConvert()) {
            return bigDecimalConversion.convertExpression(target);
        } else if (target instanceof FieldToAccessorTExpr) {
            return target;
        } else if (target instanceof VariableDeclaratorTExpr) {
            return target;
        } else if (target instanceof MapPutExprT) {
            return target;
        } else {
            return new AssignExprT(n.getOperator(), target, rhsOrNull());
        }
    }

    @Override
    public TypedExpression visit(ArrayAccessExpr n, Void arg) {
        if (parentIsExpressionStmt(n)) {
            return rhsOrError();
        }

        TypedExpression name = n.getName().accept(this, arg);

        Optional<Type> type = name.getType();
        if(type.filter(TypeUtils::isCollection).isPresent()) {
            Expression index = n.getIndex();
            if(index.isStringLiteralExpr() || index.isNameExpr()) {
                return new MapPutExprT(name, index, rhsOrNull(), name.getType());
            } else {
                return new ListAccessExprT(name, index, type.get());
            }
        }
        return new UnalteredTypedExpression(n, type.orElse(null));
    }


    @Override
    public TypedExpression visit(IfStmt n, Void arg) {
        return new UnalteredTypedExpression(n);
    }

    private TypedExpression rhsOrNull() {
        return rhs.orElse(null);
    }

    private TypedExpression rhsOrError() {
        return rhs.orElseThrow(() -> new MvelCompilerException("RHS not found, need a valid expression"));
    }

    /*
        This means there's no LHS
     */
    private boolean parentIsExpressionStmt(Node n) {
        return n.getParentNode().filter(p -> p instanceof ExpressionStmt).isPresent();
    }

    private boolean parentIsArrayAccessExpr(Node n) {
        return n.getParentNode().filter(p -> p instanceof ArrayAccessExpr).isPresent();
    }

    private Class<?> getRHSType() {
        return rhs
                .flatMap(TypedExpression::getType)
                .map(TypeUtils::classFromType)
                .orElseThrow(() -> new MvelCompilerException("RHS doesn't have a type"));
    }

    private Class<?> getRHSorLHSType(VariableDeclarator n) {
        return mvelCompilerContext.resolveType(n.getType().asString());
    }

    private void logPhase(String phase, Node statement) {
        if(logger.isDebugEnabled()) {
            logger.debug(phase, printConstraint(statement));
        }
    }
}


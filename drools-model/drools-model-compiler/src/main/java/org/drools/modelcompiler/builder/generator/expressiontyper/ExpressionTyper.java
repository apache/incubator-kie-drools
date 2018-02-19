package org.drools.modelcompiler.builder.generator.expressiontyper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.drools.core.util.ClassUtils;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Node;
import org.drools.javaparser.ast.NodeList;
import org.drools.javaparser.ast.drlx.expr.HalfBinaryExpr;
import org.drools.javaparser.ast.drlx.expr.HalfPointFreeExpr;
import org.drools.javaparser.ast.drlx.expr.InlineCastExpr;
import org.drools.javaparser.ast.drlx.expr.NullSafeFieldAccessExpr;
import org.drools.javaparser.ast.drlx.expr.PointFreeExpr;
import org.drools.javaparser.ast.expr.BinaryExpr;
import org.drools.javaparser.ast.expr.CastExpr;
import org.drools.javaparser.ast.expr.EnclosedExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.FieldAccessExpr;
import org.drools.javaparser.ast.expr.InstanceOfExpr;
import org.drools.javaparser.ast.expr.LiteralExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.NullLiteralExpr;
import org.drools.javaparser.ast.expr.SimpleName;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.ThisExpr;
import org.drools.javaparser.ast.expr.UnaryExpr;
import org.drools.javaparser.ast.type.ReferenceType;
import org.drools.javaparser.printer.PrintUtil;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.operatorspec.CustomOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.OperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.TemporalOperatorSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Optional.of;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRootNodeViaParent;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getLiteralExpressionType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.nameExprToMethodCallExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.prepend;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.replaceAllHalfBinaryChildren;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.returnTypeOfMethodCallExpr;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.trasformHalfBinaryToBinary;

public class ExpressionTyper {

    private final RuleContext ruleContext;
    private final PackageModel packageModel;
    private Class<?> patternType;
    private String bindingId;
    private boolean isPositional;
    private final ExpressionTyperContext context;
    private final List<String> usedDeclarations;
    private final Set<String> reactOnProperties;
    private final List<Expression> prefixExpressions;

    private static final Logger logger          = LoggerFactory.getLogger(ExpressionTyper.class);


    public ExpressionTyper(RuleContext ruleContext, Class<?> patternType, String bindingId, boolean isPositional) {
        this(ruleContext, patternType, bindingId, isPositional, new ExpressionTyperContext());
    }

    public ExpressionTyper(RuleContext ruleContext, Class<?> patternType, String bindingId, boolean isPositional, ExpressionTyperContext context) {
        this.ruleContext = ruleContext;
        packageModel = ruleContext.getPackageModel();
        this.patternType = patternType;
        this.bindingId = bindingId;
        this.isPositional = isPositional;
        this.context = context;
        this.usedDeclarations = context.getUsedDeclarations();
        this.reactOnProperties = context.getReactOnProperties();
        this.prefixExpressions = context.getPrefixExpresssions();
    }

    public TypedExpressionResult toTypedExpression(Expression drlxExpr) {
        logger.debug("Typed expression Input: drlxExpr = {} , patternType = {} ,declarations = {}", PrintUtil.toDrlx(drlxExpr), patternType, usedDeclarations);
        final Optional<TypedExpression> typedExpression = toTypedExpressionRec(drlxExpr);
        final TypedExpressionResult typedExpressionResult = new TypedExpressionResult(typedExpression, context);
        logger.debug("Typed expression Output: {}", typedExpressionResult);
        return typedExpressionResult;
    }

    private Optional<TypedExpression> toTypedExpressionRec(Expression drlxExpr) {

        Class<?> typeCursor = patternType;

        if(drlxExpr instanceof EnclosedExpr) {
            drlxExpr = ((EnclosedExpr) drlxExpr).getInner();
        }

        if (drlxExpr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) drlxExpr;
            Optional<TypedExpression> optTypedExpr = toTypedExpressionRec(unaryExpr.getExpression());
            return optTypedExpr.map(typedExpr -> new TypedExpression( new UnaryExpr( typedExpr.getExpression(), unaryExpr.getOperator() ), typedExpr.getType() ));

        } else if (drlxExpr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) drlxExpr;

            BinaryExpr.Operator operator = binaryExpr.getOperator();

            Optional<TypedExpression> optLeft = toTypedExpressionRec(binaryExpr.getLeft());
            Optional<TypedExpression> optRight = toTypedExpressionRec(binaryExpr.getRight());

            return optLeft.flatMap(left -> optRight.flatMap(right -> {
                final BinaryExpr combo = new BinaryExpr(left.getExpression(), right.getExpression(), operator);
                return of(new TypedExpression(combo, left.getType()));
            }));

        } else if (drlxExpr instanceof HalfBinaryExpr) {
            final Expression binaryExpr = trasformHalfBinaryToBinary(drlxExpr);
            return toTypedExpressionRec(binaryExpr);

        } else if (drlxExpr instanceof LiteralExpr) {
            return of(new TypedExpression(drlxExpr, getLiteralExpressionType( ( LiteralExpr ) drlxExpr )));

        } else if (drlxExpr instanceof ThisExpr) {
            return of(new TypedExpression(new NameExpr("_this"), patternType));

        } else if (drlxExpr instanceof CastExpr) {
            CastExpr castExpr = (CastExpr)drlxExpr;
            toTypedExpressionRec(castExpr.getExpression());
            return of(new TypedExpression(castExpr, getClassFromContext(ruleContext.getTypeResolver(), castExpr.getType().asString())));

        } else if (drlxExpr instanceof NameExpr) {
            String name = drlxExpr.toString();
            Optional<DeclarationSpec> decl = ruleContext.getDeclarationById(name);
            if (decl.isPresent()) {
                // then drlxExpr is a single NameExpr referring to a binding, e.g.: "$p1".
                usedDeclarations.add(name);
                return of(new TypedExpression(drlxExpr, decl.get().getDeclarationClass()));
            } if (ruleContext.getQueryParameters().stream().anyMatch(qp -> qp.getName().equals(name))) {
                // then drlxExpr is a single NameExpr referring to a query parameter, e.g.: "$p1".
                usedDeclarations.add(name);
                return of(new TypedExpression(drlxExpr));
            } else if(packageModel.getGlobals().containsKey(name)){
                Expression plusThis = new NameExpr(name);
                usedDeclarations.add(name);
                return of(new TypedExpression(plusThis, packageModel.getGlobals().get(name)));
            } else {
                TypedExpression expression;
                try {
                    expression = nameExprToMethodCallExpr(name, typeCursor, null);
                } catch (IllegalArgumentException e) {
                    if (isPositional || ruleContext.getQueryName().isPresent()) {
                        String unificationVariable = ruleContext.getOrCreateUnificationId(name);
                        expression = new TypedExpression(unificationVariable, typeCursor, name);
                        return of(expression);
                    }
                    return Optional.empty();
                }
                reactOnProperties.add(name);
                Expression plusThis = prepend(new NameExpr("_this"), expression.getExpression());
                return of(new TypedExpression(plusThis, expression.getType(), name));
            }
        } else if (drlxExpr instanceof FieldAccessExpr || drlxExpr instanceof MethodCallExpr) {
            return toTypedExpressionFromMethodCallOrField(drlxExpr).getTypedExpression();
        } else if (drlxExpr instanceof PointFreeExpr) {

            final PointFreeExpr pointFreeExpr = (PointFreeExpr)drlxExpr;

            Optional<TypedExpression> optLeft = toTypedExpressionRec(pointFreeExpr.getLeft());
            OperatorSpec opSpec = getOperatorSpec(drlxExpr, pointFreeExpr.getRight(), pointFreeExpr.getOperator());

            return optLeft.map(left -> new TypedExpression(opSpec.getExpression( ruleContext, pointFreeExpr, left ), left.getType())
                    .setStatic(opSpec.isStatic())
                    .setLeft(left) );

        } else if (drlxExpr instanceof HalfPointFreeExpr) {

            final HalfPointFreeExpr halfPointFreeExpr = (HalfPointFreeExpr)drlxExpr;
            Expression parentLeft = findLeftLeafOfNameExpr(halfPointFreeExpr.getParentNode().orElseThrow(UnsupportedOperationException::new));

            Optional<TypedExpression> optLeft = toTypedExpressionRec(parentLeft);
            OperatorSpec opSpec = getOperatorSpec(drlxExpr, halfPointFreeExpr.getRight(), halfPointFreeExpr.getOperator());

            final PointFreeExpr transformedToPointFree =
                    new PointFreeExpr(halfPointFreeExpr.getTokenRange().get(),
                                      parentLeft,
                                      halfPointFreeExpr.getRight(),
                                      halfPointFreeExpr.getOperator(),
                                      halfPointFreeExpr.isNegated(),
                                      halfPointFreeExpr.getArg1(),
                                      halfPointFreeExpr.getArg2(),
                                      halfPointFreeExpr.getArg3(),
                                      halfPointFreeExpr.getArg4()
                    );

            return optLeft.map(left ->
                                       new TypedExpression(opSpec.getExpression(ruleContext, transformedToPointFree, left ), left.getType())
                                               .setStatic(opSpec.isStatic())
                                               .setLeft(left));
        }

        throw new UnsupportedOperationException();
    }

    private OperatorSpec getOperatorSpec( Expression drlxExpr, NodeList<Expression> rightExpressions, SimpleName expressionOperator) {
        for (Expression rightExpr : rightExpressions) {
            toTypedExpressionRec(rightExpr);
        }

        String operator = expressionOperator.asString();
        OperatorSpec opSpec = null;
        if (ModelGenerator.temporalOperators.contains(operator )) {
            opSpec = TemporalOperatorSpec.INSTANCE;
        } else if ( org.drools.model.functions.Operator.Register.hasOperator( operator ) ) {
            opSpec = CustomOperatorSpec.INSTANCE;
        }
        if (opSpec == null) {
            throw new UnsupportedOperationException("Unknown operator '" + operator + "' in expression: " + toDrlx(drlxExpr));
        }
        return opSpec;
    }

    private TypedExpressionResult toTypedExpressionFromMethodCallOrField(Expression drlxExpr) {
        final List<Node> childrenNodes = flattenScope(drlxExpr);
        final Node firstChild = childrenNodes.get(0);

        boolean isInLineCast = firstChild instanceof InlineCastExpr;
        final Class<?> originalTypeCursor;
        final Node firstNode;
        if (isInLineCast) {
            InlineCastExpr inlineCast = (InlineCastExpr) firstChild;
            originalTypeCursor = originalTypeCursorFromInlineCast(inlineCast);
            firstNode = inlineCast.getExpression();
        } else {
            originalTypeCursor = patternType;
            firstNode = firstChild;
        }

        final Optional<TypedExpressionCursor> teCursor = processFirstNode(drlxExpr, childrenNodes, firstNode, isInLineCast, originalTypeCursor);

        Expression previous;
        Class<?> typeCursor;
        if(!teCursor.isPresent()) {
            return new TypedExpressionResult(Optional.empty(), context);
        } else {
            previous = teCursor.get().expressionCursor;
            typeCursor = teCursor.get().typeCursor;
        }

        List<Node> childrenWithoutFirst = childrenNodes.subList(1, childrenNodes.size());
        for (Node part : childrenWithoutFirst) {
            if(typeCursor.isEnum()) {
                previous = drlxExpr;
            } else if (part instanceof SimpleName) {
                String field = part.toString();
                TypedExpression expression = nameExprToMethodCallExpr(field, typeCursor, previous);
                typeCursor = expression.getType();
                previous = expression.getExpression();
            } else if (part instanceof MethodCallExpr) {
                MethodCallExpr methodCallExprPart = (MethodCallExpr) part;
                typeCursor = returnTypeOfMethodCallExpr(ruleContext, ruleContext.getTypeResolver(), (MethodCallExpr) part, typeCursor, usedDeclarations);
                methodCallExprPart.setScope(previous);
                previous = methodCallExprPart;
            } else if (part instanceof InlineCastExpr && ((InlineCastExpr) part).getExpression() instanceof FieldAccessExpr) {
                InlineCastExpr inlineCastExprPart = (InlineCastExpr) part;
                final FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) inlineCastExprPart.getExpression();
                final TypedExpression toMethodCallExpr = nameExprToMethodCallExpr(fieldAccessExpr.getNameAsString(), typeCursor, previous);
                final Class<?> castClass = getClassFromType(ruleContext.getTypeResolver(), inlineCastExprPart.getType());
                previous = addCastToExpression(castClass, toMethodCallExpr.getExpression());
            } else {
                throw new UnsupportedOperationException();
            }
        }

        TypedExpression typedExpression = new TypedExpression();
        final Optional<TypedExpression> typedExpression1 = of(typedExpression.setExpression(previous).setType(typeCursor));

        return new TypedExpressionResult(typedExpression1, context);
    }

    private void extractPrefixExpressions(NullSafeFieldAccessExpr drlxExpr, Expression previous) {
        final BinaryExpr prefixExpression = new BinaryExpr(previous, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS);
        prefixExpressions.add(prefixExpression);

        final Expression scope = drlxExpr.getScope();
        if(scope != null) {
            final Optional<TypedExpression> typedExpression1 = toTypedExpressionRec(scope);
            typedExpression1.ifPresent(te -> {
                final Expression expression = te.getExpression();
                final BinaryExpr notNullScope = new BinaryExpr(expression, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS);
                prefixExpressions.add(0, notNullScope);
            });
        }
    }

    private Optional<TypedExpressionCursor> processFirstNode(Expression drlxExpr, List<Node> childNodes, Node firstNode, boolean isInLineCast, Class<?> originalTypeCursor) {
        final Optional<TypedExpressionCursor> result;
        if (firstNode instanceof ThisExpr || (firstNode instanceof NameExpr && firstNode.toString().equals(bindingId ))) {
            result = of(thisExpr(drlxExpr, childNodes, isInLineCast, originalTypeCursor));
        } else if (firstNode instanceof NameExpr) {
            result = nameExpr(drlxExpr, (NameExpr) firstNode, isInLineCast, originalTypeCursor);
        } else if (firstNode instanceof FieldAccessExpr && ((FieldAccessExpr) firstNode).getScope() instanceof ThisExpr) {
            result = of(fieldAccessExpr((FieldAccessExpr) firstNode, originalTypeCursor));
        } else if (firstNode instanceof MethodCallExpr) {
            result = of(methodCallExpr((MethodCallExpr) firstNode, originalTypeCursor));
        } else if (firstNode instanceof StringLiteralExpr) {
            result = of(stringLiteralExpr((StringLiteralExpr) firstNode));
        } else {
            throw new UnsupportedOperationException("Unknown node: " + firstNode);
        }

        result.ifPresent(te -> {
            if (drlxExpr instanceof NullSafeFieldAccessExpr) {
                extractPrefixExpressions((NullSafeFieldAccessExpr) drlxExpr, te.expressionCursor);
            }
        });

        return result.map(te -> {
            if (isInLineCast) {
                Expression exprWithInlineCast = addCastToExpression(te.typeCursor, te.expressionCursor);
                return new TypedExpressionCursor(exprWithInlineCast, te.typeCursor);
            } else {
                return te;
            }
        });
    }

    private Class<?> originalTypeCursorFromInlineCast(InlineCastExpr inlineCast) {
        Class<?> originalTypeCursor;
        try {
            originalTypeCursor = ruleContext.getTypeResolver().resolveType(inlineCast.getType().toString());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return originalTypeCursor;
    }

    private TypedExpressionCursor stringLiteralExpr(StringLiteralExpr firstNode) {
        TypedExpressionCursor teCursor;
        final Class<?> typeCursor = String.class;
        teCursor = new TypedExpressionCursor(firstNode, typeCursor);
        return teCursor;
    }

    private TypedExpressionCursor methodCallExpr(MethodCallExpr firstNode, Class<?> originalTypeCursor) {
        TypedExpressionCursor teCursor;
        Expression previous = new NameExpr("_this");
        firstNode.setScope(previous);
        final Class<?> typeCursor = returnTypeOfMethodCallExpr(ruleContext, ruleContext.getTypeResolver(), firstNode, originalTypeCursor, usedDeclarations);
        teCursor = new TypedExpressionCursor(firstNode, typeCursor);
        return teCursor;
    }

    private TypedExpressionCursor fieldAccessExpr(FieldAccessExpr firstNode, Class<?> originalTypeCursor) {
        TypedExpressionCursor teCursor;
        final Class<?> tc4 = originalTypeCursor;
        String firstName = firstNode.getName().getIdentifier();
        Method firstAccessor = ClassUtils.getAccessor(tc4, firstName);
        if (firstAccessor != null) {
            reactOnProperties.add(firstName);
            teCursor = new TypedExpressionCursor(new MethodCallExpr(new NameExpr("_this"), firstAccessor.getName()), firstAccessor.getReturnType());
        } else {
            throw new UnsupportedOperationException("firstNode I don't know about");
            // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
        }
        return teCursor;
    }

    private Optional<TypedExpressionCursor> nameExpr(Expression drlxExpr, NameExpr firstNode, boolean isInLineCast, Class<?> originalTypeCursor) {
        Optional<TypedExpressionCursor> teCursor;
        String firstName = firstNode.getName().getIdentifier();
        Optional<DeclarationSpec> declarationById = ruleContext.getDeclarationById(firstName);
        if (declarationById.isPresent()) {
            // do NOT append any reactOnProperties.
            // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
            usedDeclarations.add(firstName);
            final Class<?> typeCursor;
            if (!isInLineCast) {
                typeCursor = declarationById.get().getDeclarationClass();
            } else {
                typeCursor = originalTypeCursor;
            }
            teCursor = of(new TypedExpressionCursor(new NameExpr(firstName), typeCursor));
        } else {

            final Class<?> typeCursor;

            // In OOPath a declaration is based on a position rather than a name.
            // Only an OOPath chunk can have a backreference expression
            Optional<DeclarationSpec> backReference = Optional.empty();
            if(firstNode.getBackReferencesCount()  > 0) {
                List<DeclarationSpec> ooPathDeclarations = ruleContext.getOOPathDeclarations();
                DeclarationSpec backReferenceDeclaration = ooPathDeclarations.get(ooPathDeclarations.size() - 1 - firstNode.getBackReferencesCount());
                typeCursor = backReferenceDeclaration.getDeclarationClass();
                backReference = of(backReferenceDeclaration);
                usedDeclarations.add(backReferenceDeclaration.getBindingId());
            } else {
                typeCursor = originalTypeCursor;
            }

            Method firstAccessor = ClassUtils.getAccessor((!isInLineCast) ? typeCursor : patternType, firstName);
            if (firstAccessor != null) {
                // Hack to review - if a property is upper case it's probably not a react on property
                if(!"".equals(firstName) && Character.isLowerCase(firstName.charAt(0))) {
                    reactOnProperties.add(firstName);
                }
                final Class<?> typeOfFirstAccessor;
                if (!isInLineCast) {
                    typeOfFirstAccessor = firstAccessor.getReturnType();
                } else {
                    typeOfFirstAccessor = typeCursor;
                }
                NameExpr thisAccessor = new NameExpr("_this");
                final NameExpr scope = backReference.map(d -> new NameExpr(d.getBindingId())).orElse(thisAccessor);
                teCursor = of(new TypedExpressionCursor(new MethodCallExpr(scope, firstAccessor.getName()), typeOfFirstAccessor));
            } else {
                final Optional<Node> rootNode = findRootNodeViaParent(drlxExpr);
                rootNode.ifPresent(n -> {
                    // In the error messages HalfBinary are transformed to Binary
                    Node withHalfBinaryReplaced = replaceAllHalfBinaryChildren(n);
                    ruleContext.addCompilationError(new ParseExpressionErrorResult((Expression) withHalfBinaryReplaced));
                });
                teCursor = Optional.empty();
            }
        }
        return teCursor;
    }

    private TypedExpressionCursor thisExpr(Expression drlxExpr, List<Node> childNodes, boolean isInLineCast, Class<?> originalTypeCursor) {
        TypedExpressionCursor teCursor;
        if (childNodes.size() > 1 && !isInLineCast) {
            SimpleName fieldName = null;
            if (childNodes.get(1) instanceof NameExpr) {
                fieldName = (( NameExpr ) childNodes.get(1 )).getName();
            } else if (childNodes.get(1) instanceof SimpleName) {
                fieldName = ( SimpleName ) childNodes.get(1 );
            }
            if (fieldName != null) {
                reactOnProperties.add( getFieldName(drlxExpr, fieldName ) );
            }
        }
        teCursor = new TypedExpressionCursor(new NameExpr("_this"), originalTypeCursor);
        return teCursor;
    }

    private Expression addCastToExpression(Class<?> typeCursor, Expression previous) {
        ReferenceType castType = JavaParser.parseClassOrInterfaceType(typeCursor.getName());
        prefixExpressions.add(new InstanceOfExpr(previous, castType));
        previous = new EnclosedExpr(new CastExpr(castType, previous));
        return previous;
    }

    private static String getFieldName( Expression drlxExpr, SimpleName fieldName ) {
        if ( drlxExpr instanceof MethodCallExpr ) {
            String name = getter2property( fieldName.getIdentifier() );
            if ( name != null ) {
                return name;
            }
        }
        return fieldName.getIdentifier();
    }

    public static Expression findLeftLeafOfNameExpr(Node expression) {
        if(expression instanceof BinaryExpr) {
            BinaryExpr be = (BinaryExpr)expression;
            return findLeftLeafOfNameExpr(be.getLeft());
        } else if(expression instanceof NameExpr) {
            return (Expression) expression;
        } else if(expression instanceof ThisExpr) {
            return (Expression) expression;
        } else if(expression instanceof PointFreeExpr) {
            return findLeftLeafOfNameExpr(((PointFreeExpr) expression).getLeft());
        } else {
            throw new UnsupportedOperationException("Unknown expression: " + expression);
        }
    }

    private static List<Node> flattenScope(Expression expressionWithScope) {
        List<Node> res = new ArrayList<>();
        if (expressionWithScope instanceof FieldAccessExpr) {
            FieldAccessExpr fieldAccessExpr = (FieldAccessExpr) expressionWithScope;
            res.addAll(flattenScope(fieldAccessExpr.getScope()));
            res.add(fieldAccessExpr.getName());
        } else if (expressionWithScope instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) expressionWithScope;
            if (methodCallExpr.getScope().isPresent()) {
                res.addAll(flattenScope(methodCallExpr.getScope().get()));
            }
            res.add(methodCallExpr.setScope(null));
        } else if (expressionWithScope instanceof InlineCastExpr && ((InlineCastExpr) expressionWithScope).getExpression() instanceof FieldAccessExpr) {
            InlineCastExpr inlineCastExpr = (InlineCastExpr) expressionWithScope;
            Expression internalScope = ((FieldAccessExpr)inlineCastExpr.getExpression()).getScope();
            res.addAll(flattenScope((internalScope)));
            res.add(expressionWithScope);
        } else {
            res.add(expressionWithScope);
        }
        return res;
    }


    static class TypedExpressionCursor {
        public Expression expressionCursor;
        public Class<?> typeCursor;

        public TypedExpressionCursor(Expression expressionCursor, Class<?> typeCursor) {
            this.expressionCursor = expressionCursor;
            this.typeCursor = typeCursor;
        }
    }
}

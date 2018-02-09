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
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.errors.ParseExpressionErrorResult;
import org.drools.modelcompiler.builder.generator.DeclarationSpec;
import org.drools.modelcompiler.builder.generator.ModelGenerator;
import org.drools.modelcompiler.builder.generator.RuleContext;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.builder.generator.operatorspec.CustomOperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.OperatorSpec;
import org.drools.modelcompiler.builder.generator.operatorspec.TemporalOperatorSpec;

import static java.util.Optional.of;
import static org.drools.core.util.ClassUtils.getter2property;
import static org.drools.javaparser.printer.PrintUtil.toDrlx;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.findRootNodeViaParent;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
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
    private final List<Expression> prefixExpresssions;

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
        this.prefixExpresssions = context.getPrefixExpresssions();
    }

    public TypedExpressionResult toTypedExpression(Expression drlxExpr) {
        final Optional<TypedExpression> typedExpression = toTypedExpressionRec(drlxExpr);
        return new TypedExpressionResult(typedExpression, context);
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

            return optLeft.map(left -> new TypedExpression(opSpec.getExpression( pointFreeExpr, left ), left.getType())
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
                                       new TypedExpression(opSpec.getExpression(transformedToPointFree, left ), left.getType())
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
        Class<?> typeCursor = patternType;

        List<Node> childNodes = flattenScope(drlxExpr);
        Node firstNode = childNodes.get(0);

        boolean isInLineCast = firstNode instanceof InlineCastExpr;
        if (isInLineCast) {
            InlineCastExpr inlineCast = (InlineCastExpr) firstNode;
            try {
                typeCursor = ruleContext.getTypeResolver().resolveType(inlineCast.getType().toString());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            firstNode = inlineCast.getExpression();
        }

        Expression previous;

        if (firstNode instanceof ThisExpr || (firstNode instanceof NameExpr && firstNode.toString().equals( bindingId ))) {
            previous = new NameExpr("_this");
            if (childNodes.size() > 1 && !isInLineCast) {
                SimpleName fieldName = null;
                if (childNodes.get(1) instanceof NameExpr) {
                    fieldName = (( NameExpr ) childNodes.get( 1 )).getName();
                } else if (childNodes.get(1) instanceof SimpleName) {
                    fieldName = ( SimpleName ) childNodes.get( 1 );
                }
                if (fieldName != null) {
                    reactOnProperties.add( getFieldName( drlxExpr, fieldName ) );
                }
            }
        } else if (firstNode instanceof NameExpr) {
            NameExpr firstNodeName = (NameExpr) firstNode;
            String firstName = firstNodeName.getName().getIdentifier();
            Optional<DeclarationSpec> declarationById = ruleContext.getDeclarationById(firstName);
            if (declarationById.isPresent()) {
                // do NOT append any reactOnProperties.
                // because reactOnProperties is referring only to the properties of the type of the pattern, not other declarations properites.
                usedDeclarations.add(firstName);
                if (!isInLineCast) {
                    typeCursor = declarationById.get().getDeclarationClass();
                }
                previous = new NameExpr(firstName);
            } else {

                // In OOPath a declaration is based on a position rather than a name.
                // Only an OOPath chunk can have a backreference expression
                Optional<DeclarationSpec> backReference = Optional.empty();
                if(firstNodeName.getBackReferencesCount()  > 0) {
                    List<DeclarationSpec> ooPathDeclarations = ruleContext.getOOPathDeclarations();
                    DeclarationSpec backReferenceDeclaration = ooPathDeclarations.get(ooPathDeclarations.size() - 1 - firstNodeName.getBackReferencesCount());
                    typeCursor = backReferenceDeclaration.getDeclarationClass();
                    backReference = of(backReferenceDeclaration);
                    usedDeclarations.add(backReferenceDeclaration.getBindingId());
                }

                Method firstAccessor = ClassUtils.getAccessor((!isInLineCast) ? typeCursor : patternType, firstName);
                if (firstAccessor != null) {
                    // Hack to review - if a property is upper case it's probably not a react on property
                    if(!"".equals(firstName) && Character.isLowerCase(firstName.charAt(0))) {
                        reactOnProperties.add(firstName);
                    }
                    if (!isInLineCast) {
                        typeCursor = firstAccessor.getReturnType();
                    }
                    NameExpr thisAccessor = new NameExpr("_this");
                    final NameExpr scope = backReference.map(d -> new NameExpr(d.getBindingId())).orElse(thisAccessor);
                    previous = new MethodCallExpr(scope, firstAccessor.getName());
                } else {
                    final Optional<Node> rootNode = findRootNodeViaParent(drlxExpr);
                    rootNode.ifPresent(n -> {
                        // In the error messages HalfBinary are transformed to Binary
                        Node withHalfBinaryReplaced = replaceAllHalfBinaryChildren(n);
                        ruleContext.addCompilationError(new ParseExpressionErrorResult((Expression) withHalfBinaryReplaced));
                    });
                    return new TypedExpressionResult(Optional.empty(), context);
                }
            }
        } else if (firstNode instanceof FieldAccessExpr && ((FieldAccessExpr) firstNode).getScope() instanceof ThisExpr) {
            String firstName = ((FieldAccessExpr) firstNode).getName().getIdentifier();
            Method firstAccessor = ClassUtils.getAccessor(typeCursor, firstName);
            if (firstAccessor != null) {
                reactOnProperties.add(firstName);
                typeCursor = firstAccessor.getReturnType();
                previous = new MethodCallExpr(new NameExpr("_this"), firstAccessor.getName());
            } else {
                throw new UnsupportedOperationException("firstNode I don't know about");
                // TODO would it be fine to assume is a global if it's not in the declarations and not the first accesssor in a chain?
            }
        } else if (firstNode instanceof SimpleName) {
            SimpleName fieldName = ( SimpleName ) firstNode;
            String name = getFieldName( drlxExpr, fieldName );
            reactOnProperties.add( name );
            TypedExpression expression = nameExprToMethodCallExpr(name, typeCursor, null);
            Expression plusThis = prepend(new NameExpr("_this"), expression.getExpression());
            if (childNodes.size() != 1) {
                throw new UnsupportedOperationException("then the below should not be a return");
            }
            return new TypedExpressionResult( of(new TypedExpression(plusThis, expression.getType())), context);
        } else if (firstNode instanceof MethodCallExpr) {
            MethodCallExpr methodCallExpr = (MethodCallExpr) firstNode;
            previous = new NameExpr("_this");
            methodCallExpr.setScope(previous);
            typeCursor = returnTypeOfMethodCallExpr(ruleContext, ruleContext.getTypeResolver(), methodCallExpr, typeCursor, usedDeclarations);
            previous = methodCallExpr;
        } else if (firstNode instanceof StringLiteralExpr) {
            typeCursor = String.class;
            previous = (( StringLiteralExpr ) firstNode);
        } else {
            throw new UnsupportedOperationException("Unknown node: " + firstNode);
        }

        childNodes = childNodes.subList(1, childNodes.size());

        TypedExpression typedExpression = new TypedExpression();
        if (isInLineCast) {
            ReferenceType castType = JavaParser.parseClassOrInterfaceType(typeCursor.getName());
            prefixExpresssions.add(new InstanceOfExpr(previous, castType));
            previous = new EnclosedExpr(new CastExpr(castType, previous));
        }
        if (drlxExpr instanceof NullSafeFieldAccessExpr) {
            final BinaryExpr prefixExpression = new BinaryExpr(previous, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS);
            prefixExpresssions.add(prefixExpression);

            final Expression scope = ((NullSafeFieldAccessExpr) drlxExpr).getScope();
            if(scope != null) {
                final Optional<TypedExpression> typedExpression1 = toTypedExpressionRec(scope);
                typedExpression1.ifPresent(te -> {
                    final Expression expression = te.getExpression();
                    final BinaryExpr notNullScope = new BinaryExpr(expression, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS);
                    prefixExpresssions.add(0, notNullScope);
                });
            }
        }

        for (Node part : childNodes) {
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
            } else {
                throw new UnsupportedOperationException();
            }
        }

        return new TypedExpressionResult(of(typedExpression.setExpression(previous).setType(typeCursor)), context);
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
        } else {
            res.add(expressionWithScope);
        }
        return res;
    }


}

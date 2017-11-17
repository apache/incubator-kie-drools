package org.drools.modelcompiler.builder.generator;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.ExprConstraintDescr;
import org.drools.compiler.lang.descr.PatternDescr;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.stmt.ReturnStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.modelcompiler.builder.PackageModel;

import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.BUILD_CALL;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.QUERY_INVOCATION_CALL;
import static org.drools.modelcompiler.builder.generator.ModelGenerator.VALUE_OF_CALL;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class QueryGenerator {

    public static final String QUERY_CALL = "query";

    public static void processQueryDef(InternalKnowledgePackage pkg, PackageModel packageModel, QueryDescr queryDescr) {
        RuleContext context = new RuleContext(pkg, packageModel.getExprIdGenerator(), Optional.of(queryDescr));
        String queryName = queryDescr.getName();
        final String queryDefVariableName = toQueryDef(queryName);
        context.queryName = Optional.of(queryDefVariableName);

        parseQueryParameters(context, packageModel, queryDescr);
        ClassOrInterfaceType queryDefType = getQueryType(context.queryParameters);

        MethodCallExpr queryCall = new MethodCallExpr(null, QUERY_CALL);
        if (!queryDescr.getNamespace().isEmpty()) {
            queryCall.addArgument( new StringLiteralExpr(queryDescr.getNamespace() ) );
        }
        queryCall.addArgument(new StringLiteralExpr(queryName));
        for (QueryParameter qp : context.queryParameters) {
            queryCall.addArgument(new ClassExpr(JavaParser.parseType(qp.type.getCanonicalName())));
            queryCall.addArgument(new StringLiteralExpr(qp.name));
        }
        packageModel.getQueryDefWithType().put(queryDefVariableName, new QueryDefWithType(queryDefType, queryCall, context));
    }

    public static class QueryDefWithType {
        private ClassOrInterfaceType queryType;
        private MethodCallExpr methodCallExpr;
        private RuleContext context;

        public QueryDefWithType(ClassOrInterfaceType queryType, MethodCallExpr methodCallExpr, RuleContext contex) {
            this.queryType = queryType;
            this.methodCallExpr = methodCallExpr;
            this.context = contex;
        }

        public ClassOrInterfaceType getQueryType() {
            return queryType;
        }

        public MethodCallExpr getMethodCallExpr() {
            return methodCallExpr;
        }

        public RuleContext getContext() {
            return context;
        }
    }


    public static void processQuery(PackageModel packageModel, QueryDescr queryDescr) {
        RuleContext context = packageModel.getQueryDefWithType().get(toQueryDef(queryDescr.getName())).getContext();
        final String queryDefVariableName = toQueryDef(queryDescr.getName());

        visit(context, packageModel, queryDescr);
        final Type queryType = JavaParser.parseType(Query.class.getCanonicalName());

        MethodDeclaration queryMethod = new MethodDeclaration(EnumSet.of(Modifier.PRIVATE), queryType, "query_" + toId(queryDescr.getName()));

        BlockStmt queryBody = new BlockStmt();
        ModelGenerator.createVariables(queryBody, packageModel, context);
        queryMethod.setBody(queryBody);

        String queryBuildVarName = queryDescr.getName() + "_build";
        VariableDeclarationExpr queryBuildVar = new VariableDeclarationExpr(queryType, queryBuildVarName);

        MethodCallExpr buildCall = new MethodCallExpr(new NameExpr(queryDefVariableName), BUILD_CALL);
        context.expressions.forEach(buildCall::addArgument);

        AssignExpr queryBuildAssign = new AssignExpr(queryBuildVar, buildCall, AssignExpr.Operator.ASSIGN);
        queryBody.addStatement(queryBuildAssign);

        queryBody.addStatement(new ReturnStmt(queryBuildVarName));
        packageModel.putQueryMethod(queryMethod);
    }


    private static void parseQueryParameters(RuleContext context, PackageModel packageModel, QueryDescr descr) {
        for (int i = 0; i < descr.getParameters().length; i++) {
            final String argument = descr.getParameters()[i];
            final String type = descr.getParameterTypes()[i];
            context.addDeclaration(new DeclarationSpec(argument, getClassFromContext(context.getPkg().getTypeResolver(), type)));
            QueryParameter queryParameter = new QueryParameter(argument, getClassFromContext(context.getPkg().getTypeResolver(), type));
            context.queryParameters.add(queryParameter);
            packageModel.putQueryVariable("query_" + descr.getName(), queryParameter);
        }
    }

    private static void visit(RuleContext context, PackageModel packageModel, QueryDescr descr) {
        ModelGenerator.visit(context, packageModel, descr.getLhs());
    }

    private static ClassOrInterfaceType getQueryType(List<QueryParameter> queryParameters) {
        Class<?> res = QueryDef.getQueryClassByArity(queryParameters.size());
        ClassOrInterfaceType queryType = JavaParser.parseClassOrInterfaceType(res.getCanonicalName());

        Type[] genericType = queryParameters.stream()
                .map(e -> e.type)
                .map(DrlxParseUtil::classToReferenceType)
                .toArray(Type[]::new);

        if (genericType.length > 0) {
            queryType.setTypeArguments(genericType);
        }

        return queryType;
    }

    public static boolean bindQuery( RuleContext context, PackageModel packageModel, PatternDescr pattern, List<? extends BaseDescr> descriptors ) {
        String queryName = "query_" + pattern.getObjectType();
        MethodDeclaration queryMethod = packageModel.getQueryMethod(queryName);
        if (queryMethod != null) {
            NameExpr queryCall = new NameExpr(toQueryDef(pattern.getObjectType()));
            MethodCallExpr callCall = new MethodCallExpr(queryCall, QUERY_INVOCATION_CALL);
            callCall.addArgument( "" + !pattern.isQuery() );

            for (int i = 0; i < descriptors.size(); i++) {
                String itemText = descriptors.get(i).getText();
                if(isLiteral(itemText)) {
                    MethodCallExpr valueOfMethod = new MethodCallExpr(null, VALUE_OF_CALL);
                    valueOfMethod.addArgument(new NameExpr(itemText));
                    callCall.addArgument(valueOfMethod);
                } else {
                    QueryParameter qp = packageModel.queryVariables(queryName).get(i);
                    context.addDeclaration(new DeclarationSpec(itemText, qp.type));
                    callCall.addArgument(new NameExpr(toVar(itemText)));
                }
            }

            context.addExpression(callCall);
            return true;
        }
        return false;
    }

    public static boolean isLiteral(String value) {
        return value != null && value.length() > 0 &&
                ( Character.isDigit(value.charAt(0)) || value.charAt(0) == '"' || "true".equals(value) || "false".equals(value) || "null".equals(value) );
    }

    public static boolean createQueryCall(PackageModel packageModel, RuleContext context, PatternDescr pattern) {
        String queryDef = toQueryDef(pattern.getObjectType());
        if (packageModel.getQueryDefWithType().containsKey(queryDef)) {
            MethodCallExpr callMethod = new MethodCallExpr(new NameExpr(queryDef), QUERY_INVOCATION_CALL);
            callMethod.addArgument( "" + !pattern.isQuery() );

            List<QueryParameter> parameters = packageModel.getQueryDefWithType().get(queryDef).getContext().queryParameters;
            for (int i = 0; i < parameters.size(); i++) {
                String queryName = context.queryName.orElseThrow(RuntimeException::new);
                ExprConstraintDescr variableName = (ExprConstraintDescr) pattern.getConstraint().getDescrs().get(i);
                Optional<String> unificationId = context.getUnificationId(variableName.toString());
                int queryIndex = i + 1;
                Expression parameterCall = unificationId.map(name -> (Expression)new NameExpr(toVar(name)))
                        .orElseGet(() -> new MethodCallExpr(new NameExpr(queryName), toQueryArg(queryIndex)));
                callMethod.addArgument(parameterCall);
            }

            context.addExpression(callMethod);
            return true;
        }
        return false;
    }

    public static Expression substituteBindingWithQueryParameter(RuleContext context, String x) {
        Optional<QueryParameter> optQueryParameter = context.queryParameterWithName(p -> p.name.equals(x));
        return optQueryParameter.map(qp -> {

            final String queryDef = context.queryName.orElseThrow(RuntimeException::new);

            final int queryParameterIndex = context.queryParameters.indexOf(qp) + 1;
            return (Expression)new MethodCallExpr(new NameExpr(queryDef), toQueryArg(queryParameterIndex));

        }).orElse(new NameExpr(toVar(x)));
    }


    public static String toQueryDef(String queryName) {
        return "queryDef_" + queryName;
    }

    private static String toQueryArg(int queryParameterIndex) {
        return "getArg"+ queryParameterIndex;
    }

}

package org.drools.modelcompiler.builder.generator;

import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.QueryDescr;
import org.drools.model.Query;
import org.drools.model.QueryDef;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.visitor.ModelGeneratorVisitor;

import static com.github.javaparser.StaticJavaParser.parseType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.getClassFromContext;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.BUILD_CALL;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.QUERY_CALL;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class QueryGenerator {

    public static void processQueryDef(PackageModel packageModel, QueryDescr queryDescr, RuleContext context) {
        context.setDescr(queryDescr);
        String queryName = queryDescr.getName();
        final String queryDefVariableName = toQueryDef(queryName);
        context.setQueryName(Optional.of(queryDefVariableName));

        parseQueryParameters(context, packageModel, queryDescr);
        ClassOrInterfaceType queryDefType = getQueryType(context.getQueryParameters());

        MethodCallExpr queryCall = new MethodCallExpr(null, QUERY_CALL);
        if (!queryDescr.getNamespace().isEmpty()) {
            queryCall.addArgument( new StringLiteralExpr(queryDescr.getNamespace() ) );
        }
        queryCall.addArgument(new StringLiteralExpr(queryName));
        for (QueryParameter qp : context.getQueryParameters()) {
            queryCall.addArgument(new ClassExpr(parseType(qp.type.getCanonicalName())));
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

    public static void processQuery(KnowledgeBuilderImpl kbuilder, PackageModel packageModel, QueryDescr queryDescr) {
        String queryDefVariableName = toQueryDef(queryDescr.getName());
        RuleContext context = packageModel.getQueryDefWithType().get(queryDefVariableName).getContext();
        context.addGlobalDeclarations(packageModel.getGlobals());
        context.setDialectFromAttributes(queryDescr.getAttributes().values());

        new ModelGeneratorVisitor(context, packageModel).visit(queryDescr.getLhs());
        final Type queryType = parseType(Query.class.getCanonicalName());

        MethodDeclaration queryMethod = new MethodDeclaration(NodeList.nodeList(Modifier.privateModifier()), queryType, "query_" + toId(queryDescr.getName()));

        BlockStmt queryBody = new BlockStmt();
        ModelGenerator.createVariables(kbuilder, queryBody, packageModel, context);
        queryMethod.setBody(queryBody);

        String queryBuildVarName = toId( queryDescr.getName() ) + "_build";
        VariableDeclarationExpr queryBuildVar = new VariableDeclarationExpr(queryType, queryBuildVarName);

        MethodCallExpr buildCall = new MethodCallExpr(new NameExpr(queryDefVariableName), BUILD_CALL);
        context.getExpressions().forEach(buildCall::addArgument);

        AssignExpr queryBuildAssign = new AssignExpr(queryBuildVar, buildCall, AssignExpr.Operator.ASSIGN);
        queryBody.addStatement(queryBuildAssign);

        queryBody.addStatement(new ReturnStmt(queryBuildVarName));
        packageModel.putQueryMethod(queryMethod);
    }

    private static void parseQueryParameters(RuleContext context, PackageModel packageModel, QueryDescr descr) {
        for (int i = 0; i < descr.getParameters().length; i++) {
            final String argument = descr.getParameters()[i];
            final String type = descr.getParameterTypes()[i];
            context.addDeclaration(argument, getClassFromContext(context.getTypeResolver(), type));
            QueryParameter queryParameter = new QueryParameter(argument, getClassFromContext(context.getTypeResolver(), type));
            context.getQueryParameters().add(queryParameter);
            packageModel.putQueryVariable("query_" + toId( descr.getName() ), queryParameter);
        }
    }

    private static ClassOrInterfaceType getQueryType(List<QueryParameter> queryParameters) {
        ClassOrInterfaceType queryType = toClassOrInterfaceType( QueryDef.getQueryClassByArity(queryParameters.size()) );

        Type[] genericType = queryParameters.stream()
                .map(e -> e.type)
                .map(DrlxParseUtil::classToReferenceType)
                .toArray(Type[]::new);

        if (genericType.length > 0) {
            queryType.setTypeArguments(genericType);
        }

        return queryType;
    }

    public static boolean isLiteral(String value) {
        return value != null && value.length() > 0 &&
                ( Character.isDigit(value.charAt(0)) || value.charAt(0) == '"' || "true".equals(value) || "false".equals(value) || "null".equals(value) || value.endsWith( ".class" ) );
    }

    public static String toQueryDef(String queryName) {
        return "queryDef_" + toId( queryName );
    }

    public static String toQueryArg(int queryParameterIndex) {
        return "getArg"+ queryParameterIndex;
    }

}

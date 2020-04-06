package org.kie.dmn.typesafe;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodWithStringBody;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.StringUtils.ucFirst;

public class FeelPropertyTemplate {

    CompilationUnit methodTemplate;

    List<DMNDeclaredField> fields;

    public FeelPropertyTemplate(List<DMNDeclaredField> fields) {
        this.fields = fields;
    }

    public List<MethodDefinition> getMethods() {
        List<MethodDefinition> allMethods = new ArrayList<>();

        methodTemplate = getMethodTemplate();

        allMethods.add(getFeelPropertyDefinition());
        allMethods.add(setFeelPropertyDefinition());
        allMethods.add(fromMap());
        allMethods.add(allFeelProperties());

        return allMethods;
    }

    private MethodDefinition getFeelPropertyDefinition() {

        MethodDeclaration getFEELProperty = cloneMethodTemplate("getFEELProperty");

        SwitchStmt firstSwitch = getFEELProperty.findFirst(SwitchStmt.class).orElseThrow(RuntimeException::new);

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toGetPropertySwitchEntry).collect(Collectors.toList());

        SwitchEntry defaultSwitchStmt = firstSwitch.findFirst(SwitchEntry.class, sw -> sw.getLabels().isEmpty()).orElseThrow(RuntimeException::new); // default
        collect.add(defaultSwitchStmt);

        firstSwitch.setEntries(nodeList(collect));

        String body = getFEELProperty.getBody().orElseThrow(RuntimeException::new).toString();
        MethodWithStringBody getFeelPropertyDefinition = new MethodWithStringBody("getFEELProperty", EvalHelper.PropertyValueResult.class.getCanonicalName(), body);
        getFeelPropertyDefinition.addParameter(String.class.getCanonicalName(), "property");

        addOverrideAnnotation(getFeelPropertyDefinition);

        return getFeelPropertyDefinition;
    }

    private void addOverrideAnnotation(MethodWithStringBody md) {
        md.addAnnotation("Override");
    }

    private MethodDeclaration cloneMethodTemplate(String getFEELProperty) {
        return methodTemplate.findFirst(MethodDeclaration.class, mc -> mc.getNameAsString().equals(getFEELProperty))
                .orElseThrow(RuntimeException::new)
                .clone();
    }

    private SwitchEntry toGetPropertySwitchEntry(FieldDefinition fieldDefinition) {
        ReturnStmt returnStmt = new ReturnStmt();
        MethodCallExpr mc = StaticJavaParser.parseExpression(EvalHelper.PropertyValueResult.class.getCanonicalName() + ".ofValue()");
        String accessorName = getAccessorName(fieldDefinition, "get");
        mc.addArgument(new MethodCallExpr(new ThisExpr(), accessorName));
        returnStmt.setExpression(mc);
        return new SwitchEntry(nodeList(new StringLiteralExpr(fieldDefinition.getFieldName())), SwitchEntry.Type.STATEMENT_GROUP, nodeList(returnStmt));
    }

    private MethodDefinition setFeelPropertyDefinition() {

        MethodDeclaration setFEELProperty = cloneMethodTemplate("setFEELProperty");

        SwitchStmt firstSwitch = setFEELProperty.findFirst(SwitchStmt.class).orElseThrow(RuntimeException::new);

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toSetPropertySwitchEntry).collect(Collectors.toList());

        firstSwitch.setEntries(nodeList(collect));

        String body = setFEELProperty.getBody().orElseThrow(RuntimeException::new).toString();
        MethodWithStringBody setFeelPropertyDefinition = new MethodWithStringBody("setFEELProperty", "void", body);
        setFeelPropertyDefinition.addParameter(String.class.getCanonicalName(), "property");
        setFeelPropertyDefinition.addParameter(Object.class.getCanonicalName(), "value");
        addOverrideAnnotation(setFeelPropertyDefinition);

        return setFeelPropertyDefinition;
    }

    private SwitchEntry toSetPropertySwitchEntry(FieldDefinition fieldDefinition) {

        String accessorName = getAccessorName(fieldDefinition, "set");
        MethodCallExpr setMethod = new MethodCallExpr(new ThisExpr(), accessorName);
        setMethod.addArgument(new CastExpr(StaticJavaParser.parseType(fieldDefinition.getObjectType()), new NameExpr("value")));

        ExpressionStmt setStatement = new ExpressionStmt();
        setStatement.setExpression(setMethod);

        NodeList<Expression> labels = nodeList(new StringLiteralExpr(fieldDefinition.getFieldName()));
        NodeList<Statement> statements = nodeList(setStatement, new ReturnStmt());
        return new SwitchEntry(labels, SwitchEntry.Type.STATEMENT_GROUP, statements);
    }

    private MethodDefinition fromMap() {


        MethodDeclaration allFeelProperties = cloneMethodTemplate("fromMap");

        BlockStmt originalStatements = allFeelProperties.getBody().orElseThrow(RuntimeException::new);
        BlockStmt simplePropertyBLock = (BlockStmt) originalStatements.getStatement(0);
        BlockStmt pojoPropertyBlock = (BlockStmt) originalStatements.getStatement(1);
        BlockStmt collectionsPropertyBlock = (BlockStmt) originalStatements.getStatement(2);

        List<Statement> allStmts = fields.stream().map(f -> f.createFromMapEntry(simplePropertyBLock,
                                                                                 pojoPropertyBlock,
                                                                                 collectionsPropertyBlock))
                .collect(Collectors.toList());

        BlockStmt body = new BlockStmt(nodeList(allStmts));

        MethodWithStringBody setFeelProperty = new MethodWithStringBody("fromMap", "void", body.toString());
        setFeelProperty.addParameter("java.util.Map<String, Object>", "values");


        return setFeelProperty;
    }

    private CompilationUnit getMethodTemplate() {
        InputStream resourceAsStream = this.getClass()
                .getResourceAsStream("/org/kie/dmn/core/impl/DMNTypeSafeTypeTemplate.java");
        return StaticJavaParser.parse(resourceAsStream);
    }

    private MethodWithStringBody allFeelProperties() {

        MethodDeclaration allFeelProperties = cloneMethodTemplate("allFEELProperties");

        List<Statement> collect = fields.stream().map(this::toGetAllProperty).collect(Collectors.toList());
        BlockStmt newBlockStatement = new BlockStmt(nodeList(collect));

        allFeelProperties.findAll(ExpressionStmt.class,
                                  mc -> mc.getExpression().isMethodCallExpr() &&
                                          mc.getExpression().asMethodCallExpr().getNameAsString().equals("put"))
                .forEach(n -> n.replace(newBlockStatement));

        String body = allFeelProperties.getBody().orElseThrow(RuntimeException::new).toString();

        MethodWithStringBody allFEELProperties = new MethodWithStringBody(
                "allFEELProperties",
                "java.util.Map<String, Object>",
                body
        );

        addOverrideAnnotation(allFEELProperties);
        return allFEELProperties;
    }

    private ExpressionStmt toGetAllProperty(FieldDefinition fieldDefinition) {
        String accessorName = getAccessorName(fieldDefinition, "get");

        MethodCallExpr mc = new MethodCallExpr(new NameExpr("result"), "put");

        // TODO: avoid downcast
        DMNDeclaredField dmnDeclaredField = (DMNDeclaredField) fieldDefinition;
        String fieldName = dmnDeclaredField.getOriginalMapKey();
        mc.addArgument(new StringLiteralExpr(fieldName));
        mc.addArgument(new MethodCallExpr(new ThisExpr(), accessorName));

        return new ExpressionStmt(mc);
    }

    private String getAccessorName(FieldDefinition fieldDefinition, String get) {
        return get + ucFirst(fieldDefinition.getFieldName());
    }
}

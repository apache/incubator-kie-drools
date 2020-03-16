package org.kie.dmn.typesafe;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.SwitchStmt;
import org.drools.core.util.StringUtils;
import org.drools.modelcompiler.builder.generator.declaredtype.api.AnnotationDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.FieldDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodDefinition;
import org.drools.modelcompiler.builder.generator.declaredtype.api.MethodWithStringBody;
import org.drools.modelcompiler.builder.generator.declaredtype.api.TypeDefinition;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.FEELPropertyAccessible;
import org.kie.dmn.feel.util.EvalHelper;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.drools.core.util.StringUtils.ucFirst;

class DMNDeclaredType implements TypeDefinition {

    private final DMNType dmnType;
    List<FieldDefinition> fields = new ArrayList<>();

    DMNDeclaredType(DMNType dmnType) {
        this.dmnType = dmnType;
        initFields();
    }

    @Override
    public String getTypeName() {
        return StringUtils.ucFirst(dmnType.getName());
    }

    @Override
    public List<FieldDefinition> getFields() {
        return fields;
    }

    private void initFields() {
        Map<String, DMNType> dmnFields = dmnType.getFields();
        for (Map.Entry<String, DMNType> f : dmnFields.entrySet()) {
            DMNDeclaredField dmnDeclaredField = new DMNDeclaredField(f);
            fields.add(dmnDeclaredField);
        }
    }

    @Override
    public List<FieldDefinition> getKeyFields() {
        return Collections.emptyList();
    }

    @Override
    public Optional<String> getSuperTypeName() {
        return Optional.ofNullable(dmnType.getBaseType()).map(DMNType::getName);
    }

    @Override
    public List<String> getInterfacesNames() {
        return Collections.singletonList(FEELPropertyAccessible.class.getCanonicalName());
    }

    CompilationUnit methodTemplate;

    @Override
    public List<MethodDefinition> getMethods() {
        List<MethodDefinition> allMethods = new ArrayList<>();

        methodTemplate = getMethodTemplate();

        allMethods.add(getFeelPropertyDefinition());
        allMethods.add(setFeelPropertyDefinition());
        allMethods.add(setAllDefinition());
        allMethods.add(allFeelProperties());

        return allMethods;
    }

    private MethodDefinition getFeelPropertyDefinition() {

        MethodDeclaration getFEELProperty = methodTemplate.findFirst(MethodDeclaration.class, mc -> mc.getNameAsString().equals("getFEELProperty"))
                .orElseThrow(RuntimeException::new)
                .clone();


        SwitchStmt firstSwitch = getFEELProperty.findFirst(SwitchStmt.class).orElseThrow(RuntimeException::new);

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toGetPropertySwitchEntry).collect(Collectors.toList());

        SwitchEntry defaultSwitchStmt = firstSwitch.findFirst(SwitchEntry.class, sw -> sw.getLabels().isEmpty()).orElseThrow(RuntimeException::new); // default
        collect.add(defaultSwitchStmt);

        firstSwitch.setEntries(nodeList(collect));

        String body = getFEELProperty.getBody().orElseThrow(RuntimeException::new).toString();
        MethodWithStringBody getFeelProperty = new MethodWithStringBody("getFEELProperty", EvalHelper.PropertyValueResult.class.getCanonicalName(), body);
        getFeelProperty.addParameter(String.class.getCanonicalName(), "property");

        return getFeelProperty;
    }

    private SwitchEntry toGetPropertySwitchEntry(FieldDefinition fieldDefinition) {
        ReturnStmt returnStmt = new ReturnStmt();
        MethodCallExpr mc = StaticJavaParser.parseExpression(EvalHelper.PropertyValueResult.class.getCanonicalName() + ".ofValue()");
        String accessorName = "get" + ucFirst(fieldDefinition.getFieldName());
        mc.addArgument(new MethodCallExpr(new ThisExpr(), accessorName));
        returnStmt.setExpression(mc);
        return new SwitchEntry(nodeList(new StringLiteralExpr(fieldDefinition.getFieldName())), SwitchEntry.Type.STATEMENT_GROUP, nodeList(returnStmt));
    }

    private MethodDefinition setFeelPropertyDefinition() {

        MethodDeclaration setFEELProperty = methodTemplate.findFirst(MethodDeclaration.class, mc -> mc.getNameAsString().equals("setFEELProperty"))
                .orElseThrow(RuntimeException::new)
                .clone();

        SwitchStmt firstSwitch = setFEELProperty.findFirst(SwitchStmt.class).orElseThrow(RuntimeException::new);

        firstSwitch.setComment(null);

        List<SwitchEntry> collect = fields.stream().map(this::toSetPropertySwitchEntry).collect(Collectors.toList());

        firstSwitch.setEntries(nodeList(collect));

        String body = setFEELProperty.getBody().orElseThrow(RuntimeException::new).toString();
        MethodWithStringBody getFeelProperty = new MethodWithStringBody("setFEELProperty", "void", body);
        getFeelProperty.addParameter(String.class.getCanonicalName(), "property");
        getFeelProperty.addParameter(Object.class.getCanonicalName(), "value");

        return getFeelProperty;
    }

    private SwitchEntry toSetPropertySwitchEntry(FieldDefinition fieldDefinition) {
        ExpressionStmt expressionStmt = new ExpressionStmt();
        String accessorName = "set" + ucFirst(fieldDefinition.getFieldName());
        MethodCallExpr mc = new MethodCallExpr(new ThisExpr(), accessorName);
        mc.addArgument(new CastExpr(StaticJavaParser.parseType(fieldDefinition.getObjectType()), new NameExpr("value")));
        expressionStmt.setExpression(mc);
        return new SwitchEntry(nodeList(new StringLiteralExpr(fieldDefinition.getFieldName())), SwitchEntry.Type.STATEMENT_GROUP, nodeList(expressionStmt));
    }


    private MethodDefinition setAllDefinition() {

        String body = " {  } ";
        MethodWithStringBody setFeelProperty = new MethodWithStringBody("setAll", "void", body);
        setFeelProperty.addParameter("java.util.Map<String, Object>", "values");

        return setFeelProperty;
    }

    private CompilationUnit getMethodTemplate() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/org/kie/dmn/core/impl/DMNTypeSafeTypeTemplate.java");
        CompilationUnit parse = StaticJavaParser.parse(resourceAsStream);
        return parse;
    }

    private MethodWithStringBody allFeelProperties() {
        String allFeelPropertiesBody = " { return java.util.Collections.emptyMap(); } ";

        MethodWithStringBody allFEELProperties = new MethodWithStringBody(
                "allFEELProperties",
                "java.util.Map<String, Object>",
                allFeelPropertiesBody
        );

        return allFEELProperties;
    }

    @Override
    public List<AnnotationDefinition> getAnnotationsToBeAdded() {
        return Collections.emptyList();
    }

    @Override
    public List<FieldDefinition> findInheritedDeclaredFields() {
        return Collections.emptyList();
    }
}

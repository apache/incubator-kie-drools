package org.kie.kogito.codegen.prediction;

import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import org.drools.core.util.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.feel.codegen.feel11.CodegenStringUtil;
import org.kie.kogito.codegen.di.DependencyInjectionAnnotator;
import org.kie.pmml.commons.model.KiePMMLModel;

import static com.github.javaparser.StaticJavaParser.parse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.kie.kogito.codegen.prediction.PMMLRestResourceGenerator.TEMPLATE_JAVA;

class PMMLRestResourceGeneratorTest {

    private final static String APP_CANONICAL_NAME = "APP_CANONICAL_NAME";
    private final static KiePMMLModel KIE_PMML_MODEL = getKiePMMLModelInternal();
    private static PMMLRestResourceGenerator pmmlRestResourceGenerator;
    private static ClassOrInterfaceDeclaration template = getClassOrInterfaceDeclaration();

    @BeforeAll
    public static void setup() {
        pmmlRestResourceGenerator = new PMMLRestResourceGenerator(KIE_PMML_MODEL, APP_CANONICAL_NAME);
        assertNotNull(pmmlRestResourceGenerator);
    }

    private static KiePMMLModel getKiePMMLModelInternal() {
        String modelName = "MODEL_NAME";
        return new KiePMMLModel(modelName, Collections.emptyList()) {

            @Override
            public Object evaluate(Object o, Map<String, Object> map) {
                return null;
            }
        };
    }

    private static ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration() {
        CompilationUnit clazz = parse(PMMLRestResourceGeneratorTest.class.getResourceAsStream(TEMPLATE_JAVA));
        clazz.setPackageDeclaration(CodegenStringUtil.escapeIdentifier("IDENTIFIER"));
        return clazz
                .findFirst(ClassOrInterfaceDeclaration.class)
                .orElseThrow(() -> new NoSuchElementException("Compilation unit doesn't contain a class or interface " +
                                                                      "declaration!"));
    }

    @Test
    void constructor() {
        assertTrue(pmmlRestResourceGenerator.packageName.startsWith("org.kie.kogito"));
        assertEquals(APP_CANONICAL_NAME, pmmlRestResourceGenerator.appCanonicalName);
    }

    @Test
    void generateWithDependencyInjection() {
        String retrieved = pmmlRestResourceGenerator.withDependencyInjection(getDependencyInjectionAnnotator()).generate();
        commonEvaluateGenerate(retrieved);
        String expected = "Application application;";
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void generateWithoutDependencyInjection() {
        String retrieved = pmmlRestResourceGenerator.withDependencyInjection(null).generate();
        commonEvaluateGenerate(retrieved);
        String expected = String.format("Application application = new %s();", APP_CANONICAL_NAME);
        assertTrue(retrieved.contains(expected));
    }

    @Test
    void getNameURL() {
        String expected = URLEncoder.encode(KIE_PMML_MODEL.getName()).replaceAll("\\+", "%20");
        assertEquals(expected, pmmlRestResourceGenerator.getNameURL());
    }

    @Test
    void getKiePMMLModel() {
        assertEquals(KIE_PMML_MODEL, pmmlRestResourceGenerator.getKiePMMLModel());
    }

    @Test
    void withDependencyInjection() {
        assertNull(pmmlRestResourceGenerator.annotator);
        DependencyInjectionAnnotator dependencyInjectionAnnotator = getDependencyInjectionAnnotator();
        PMMLRestResourceGenerator retrieved =
                pmmlRestResourceGenerator.withDependencyInjection(dependencyInjectionAnnotator);
        assertEquals(pmmlRestResourceGenerator, retrieved);
        assertEquals(dependencyInjectionAnnotator, pmmlRestResourceGenerator.annotator);
    }

    @Test
    void className() {
        String expected = StringUtils.capitalize(KIE_PMML_MODEL.getName()) + "Resource";
        assertEquals(expected, pmmlRestResourceGenerator.className());
    }

    @Test
    void generatedFilePath() {
        String retrieved = pmmlRestResourceGenerator.generatedFilePath();
        assertTrue(retrieved.startsWith("org/kie/kogito"));
        String expected = StringUtils.capitalize(KIE_PMML_MODEL.getName()) + "Resource.java";
        assertTrue(retrieved.endsWith(expected));
    }

    @Test
    void useInjection() {
        pmmlRestResourceGenerator.withDependencyInjection(null);
        assertFalse(pmmlRestResourceGenerator.useInjection());
        pmmlRestResourceGenerator.withDependencyInjection(getDependencyInjectionAnnotator());
        assertTrue(pmmlRestResourceGenerator.useInjection());
    }

    @Test
    void setPathValue() {
        final Optional<SingleMemberAnnotationExpr> retrievedOpt = template.findFirst(SingleMemberAnnotationExpr.class);
        assertTrue(retrievedOpt.isPresent());
        SingleMemberAnnotationExpr retrieved = retrievedOpt.get();
        assertEquals("Path", retrieved.getName().asString());
        pmmlRestResourceGenerator.setPathValue(template);
        try {
            String expected = URLEncoder.encode(KIE_PMML_MODEL.getName()).replaceAll("\\+", "%20");
            assertEquals(expected, retrieved.getMemberValue().asStringLiteralExpr().asString());
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    void setPredictionModelName() {
        assertEquals(1, template.getMethodsByName("pmml").size());
        Optional<BlockStmt> retrievedOpt = template.getMethodsByName("pmml").get(0).getBody();
        assertTrue(retrievedOpt.isPresent());
        BlockStmt retrieved = retrievedOpt.get();
        assertTrue(retrieved.getStatement(0) instanceof ExpressionStmt);
        assertTrue(retrieved.getStatement(0).asExpressionStmt().getExpression() instanceof VariableDeclarationExpr);
        VariableDeclarationExpr variableDeclarationExpr = retrieved.getStatement(0).asExpressionStmt().getExpression().asVariableDeclarationExpr();
        Optional<Expression> expressionOpt = variableDeclarationExpr.getVariable(0).getInitializer();
        assertTrue(expressionOpt.isPresent());
        assertTrue(expressionOpt.get() instanceof MethodCallExpr);
        MethodCallExpr methodCallExpr = expressionOpt.get().asMethodCallExpr();
        assertTrue(methodCallExpr.getArgument(0) instanceof StringLiteralExpr);
        pmmlRestResourceGenerator.setPredictionModelName(template);
        try {
            assertEquals(KIE_PMML_MODEL.getName(), methodCallExpr.getArgument(0).asStringLiteralExpr().asString());
        } catch (Exception e) {
            fail(e);
        }
    }

    private void commonEvaluateGenerate(String retrieved) {
        assertNotNull(retrieved);
        String expected = String.format("@Path(\"%s\")", KIE_PMML_MODEL.getName());
        assertTrue(retrieved.contains(expected));
        expected = StringUtils.capitalize(KIE_PMML_MODEL.getName()) + "Resource";
        expected = String.format("public class %s {", expected);
        assertTrue(retrieved.contains(expected));
        expected = String.format("org.kie.kogito.prediction.PredictionModel prediction = application.predictionModels" +
                                         "().getPredictionModel(\"%s\");", KIE_PMML_MODEL.getName());
        assertTrue(retrieved.contains(expected));
    }

    private DependencyInjectionAnnotator getDependencyInjectionAnnotator() {
        return new DependencyInjectionAnnotator() {
            @Override
            public <T extends NodeWithAnnotations<?>> T withNamed(T node, String name) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withApplicationComponent(T node) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withNamedApplicationComponent(T node, String name) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withSingletonComponent(T node) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withNamedSingletonComponent(T node, String name) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withInjection(T node) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withNamedInjection(T node, String name) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withOptionalInjection(T node) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withIncomingMessage(T node, String channel) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withOutgoingMessage(T node, String channel) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey) {
                return null;
            }

            @Override
            public <T extends NodeWithAnnotations<?>> T withConfigInjection(T node, String configKey,
                                                                            String defaultValue) {
                return null;
            }

            @Override
            public MethodCallExpr withMessageProducer(MethodCallExpr produceMethod, String channel, Expression event) {
                return null;
            }

            @Override
            public MethodDeclaration withInitMethod(Expression... expression) {
                return null;
            }

            @Override
            public String optionalInstanceInjectionType() {
                return null;
            }

            @Override
            public Expression optionalInstanceExists(String fieldName) {
                return null;
            }

            @Override
            public String multiInstanceInjectionType() {
                return null;
            }

            @Override
            public Expression getMultiInstance(String fieldName) {
                return null;
            }

            @Override
            public String applicationComponentType() {
                return null;
            }

            @Override
            public String emitterType(String dataType) {
                return null;
            }

            @Override
            public String objectMapperInjectorSource(String packageName) {
                return null;
            }
        };
    }
}
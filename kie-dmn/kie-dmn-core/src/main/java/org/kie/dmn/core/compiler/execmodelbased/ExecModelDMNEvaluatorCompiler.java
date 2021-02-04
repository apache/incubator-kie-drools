/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.execmodelbased;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.reflective.classloader.ProjectClassLoader;
import org.drools.ruleunit.DataSource;
import org.kie.dmn.api.core.GeneratedSource;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.compiler.DMNCompilerContext;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNEvaluatorCompiler;
import org.kie.dmn.core.compiler.DMNFEELHelper;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.memorycompiler.CompilationProblem;
import org.kie.memorycompiler.CompilationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.joining;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getCompiler;
import static org.kie.dmn.feel.codegen.feel11.CodegenStringUtil.replaceSimpleNameWith;

public class ExecModelDMNEvaluatorCompiler extends DMNEvaluatorCompiler {

    static final Logger logger = LoggerFactory.getLogger(ExecModelDMNEvaluatorCompiler.class);
    private GeneratorsEnum[] GENERATORS_WITHOUT_EXPRESSIONS = new GeneratorsEnum[] {
            GeneratorsEnum.EVALUATOR,
            GeneratorsEnum.UNIT,
            GeneratorsEnum.EXEC_MODEL,
            GeneratorsEnum.UNARY_TESTS
    };

    public ExecModelDMNEvaluatorCompiler(DMNCompilerImpl compiler) {
        super(compiler);
    }

    enum GeneratorsEnum {
        EVALUATOR("Evaluator", new EvaluatorSourceGenerator()),
        UNIT("DTUnit", new UnitSourceGenerator()),
        EXEC_MODEL("ExecModel", new ExecModelSourceGenerator()),
        UNARY_TESTS("UnaryTests", new UnaryTestsSourceGenerator()),
        FEEL_EXPRESSION("FeelExpression", new FeelExpressionSourceGenerator());

        String type;
        SourceGenerator sourceGenerator;

        GeneratorsEnum( String type, SourceGenerator sourceGenerator) {
            this.type = type;
            this.sourceGenerator = sourceGenerator;
        }
    }

    private ProjectClassLoader projectClassLoader = ProjectClassLoader.createProjectClassLoader();

    @Override
    protected DMNExpressionEvaluator compileDecisionTable( DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt ) {
        String decisionName = getDecisionTableName(dtName, dt);
        DTableModel dTableModel = new DTableModel(ctx.getFeelHelper(), model, dtName, decisionName, dt);
        AbstractModelEvaluator evaluator = generateEvaluator( ctx, dTableModel );
        if(evaluator != null) {
            evaluator.initParameters(ctx.getFeelHelper(), ctx, dTableModel, node);
        }
        return evaluator;
    }

    protected static String getDecisionTableName(String dtName, DecisionTable dt) {
        String decisionName;
        if (dt.getParent() instanceof DRGElement) {
            decisionName = dtName;
        } else {
            if (dt.getId() != null) {
                decisionName = dt.getId();
            } else {
                DMNModelInstrumentedBase cursor = dt;
                List<String> path = new ArrayList<>();
                while (!(cursor instanceof DRGElement)) {
                    int indexOf = cursor.getParent().getChildren().indexOf(cursor);
                    path.add(String.valueOf(indexOf));
                    cursor = cursor.getParent();
                }
                path.add(((DRGElement) cursor).getName());
                decisionName = path.stream().sorted(Collections.reverseOrder()).collect(Collectors.joining("/"));
            }
        }
        return decisionName;
    }

    public AbstractModelEvaluator generateEvaluator( DMNCompilerContext ctx, DTableModel dTableModel ) {
        String pkgName = dTableModel.getNamespace();
        String clasName = dTableModel.getTableName();

        MemoryFileSystem srcMfs = new MemoryFileSystem();
        MemoryFileSystem trgMfs = new MemoryFileSystem();
        String[] fileNames = new String[getGenerators().length];
        List<GeneratedSource> generatedSources = new ArrayList<>();

        generateSources(ctx, dTableModel, srcMfs, fileNames, generatedSources);

        compileGeneratedClass(srcMfs, trgMfs, fileNames);
        defineClassInClassLoader(trgMfs);
        return createInvoker(pkgName, clasName);
    }

    protected void generateSources(DMNCompilerContext ctx, DTableModel dTableModel, MemoryFileSystem srcMfs, String[] fileNames, List<GeneratedSource> generatedSources) {
        for (int i = 0; i < fileNames.length; i++) {
            GeneratorsEnum generator = getGenerators()[i];
            String className = dTableModel.getGeneratedClassName(generator);
            String fileName = "src/main/java/" + className.replace('.', '/') + ".java";
            String javaSource = generator.sourceGenerator.generate(ctx, ctx.getFeelHelper(), dTableModel);
            fileNames[i] = fileName;
            generatedSources.add(new GeneratedSource(fileName, javaSource));
            srcMfs.write(fileNames[i], javaSource.getBytes());
        }
    }

    protected GeneratorsEnum[] getGenerators() {
        return GENERATORS_WITHOUT_EXPRESSIONS;
    }

    private AbstractModelEvaluator createInvoker(String pkgName, String clasName) {
        try {
            Class<?> evalClass = projectClassLoader.loadClass(pkgName + "." + clasName + "Evaluator");
            return (AbstractModelEvaluator) evalClass.newInstance();
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unknown decision table: " + clasName, e);
        }
    }

    private void defineClassInClassLoader(MemoryFileSystem trgMfs) {
        trgMfs.getFileNames().stream().forEach(f -> projectClassLoader.defineClass(f.replace('/', '.').substring(0, f.length() - ".class".length()), trgMfs.getBytes(f)));
    }

    private void compileGeneratedClass(MemoryFileSystem srcMfs, MemoryFileSystem trgMfs, String[] fileNames) {
        CompilationResult res = getCompiler().compile(fileNames, srcMfs, trgMfs, projectClassLoader);

        CompilationProblem[] errors = res.getErrors();
        if (errors != null && errors.length > 0) {
            Stream.of(errors).forEach(System.err::println);
            throw new RuntimeException();
        }
    }

    interface SourceGenerator {
        String generate( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel );
    }

    public static class EvaluatorSourceGenerator implements SourceGenerator {
        public String generate( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel ) {
            String pkgName = dTableModel.getNamespace();
            String clasName = dTableModel.getTableName();

            StringBuilder sb = new StringBuilder();
            sb.append( "package " ).append( pkgName ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "import java.util.List;\n" );
            sb.append( "import org.drools.model.Rule;\n" );
            sb.append( "import " ).append( DMNUnit.class.getCanonicalName() ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "public class " ).append( clasName ).append( "Evaluator extends " + AbstractModelEvaluator.class.getCanonicalName() + "{\n" );
            sb.append( "\n" );
            sb.append( "    @Override\n" );
            sb.append( "    protected List<Rule> getRules() {\n" );
            sb.append( "        return " ).append( clasName ).append( "ExecModel.getRules();\n" );
            sb.append( "    }\n" );
            sb.append( "\n" );
            sb.append( "    @Override\n" );
            sb.append( "    protected DMNUnit getDMNUnit() {\n" );
            sb.append( "        return new " ).append( clasName ).append( "DTUnit();\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );

            String source = sb.toString();
            if (logger.isDebugEnabled()) {
                logger.debug( clasName + ":\n" + source );
            }
            return source;
        }
    }

    public static class ExecModelSourceGenerator implements SourceGenerator {
        public String generate( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel ) {
            String pkgName = dTableModel.getNamespace();
            String clasName = dTableModel.getTableName();

            StringBuilder sb = new StringBuilder();
            sb.append( "package " ).append( pkgName ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "import java.util.List;\n" );
            sb.append( "import " + FeelValue.class.getCanonicalName() + ";\n" );
            sb.append( "import " + DecisionTableEvaluator.class.getCanonicalName() + ";\n" );
            sb.append( "import " ).append( DataSource.class.getCanonicalName() ).append( ";\n" );
            sb.append( "import org.drools.model.*;\n" );
            sb.append( "import org.drools.modelcompiler.dsl.pattern.D;\n" );
            sb.append( "import static " ).append( pkgName ).append( "." ).append( clasName ).append( "UnaryTests.TEST_ARRAY;\n" );
            sb.append( "\n" );
            sb.append( "public class " ).append( clasName ).append( "ExecModel {\n" );
            sb.append( "\n" );
            sb.append( "    public static List<Rule> getRules() {\n" );
            sb.append( "        return java.util.Arrays.asList( " );
            sb.append( IntStream.range( 0, dTableModel.getRows().size() ).mapToObj( i -> "rule_" + clasName + "_" + i + "()" ).collect( joining(", ") ) );
            sb.append( " );\n" );
            sb.append( "    }\n" );

            int exprCounter = 0;

            sb.append( "\n" );
            sb.append( "    private static final UnitData<DecisionTableEvaluator> var_evaluator = D.unitData(DecisionTableEvaluator.class, \"evaluator\");\n" );
            for (int j = 0; j < dTableModel.getOutputSize(); j++) {
                sb.append( "    private static final UnitData<List> var_output" + j + " = D.unitData(List.class, \"output" + j + "\");\n" );
            }
            for (int j = 0; j < dTableModel.getInputSize(); j++) {
                sb.append( "    private static final UnitData<DataSource> var_input" + j + " = D.unitData(DataSource.class, \"input" + j + "\");\n" );
                sb.append( "    private static final Variable<FeelValue> var_$pattern$" + j + "$ = D.declarationOf(FeelValue.class, \"$pattern$" + j + "$\", var_input" + j + ");\n" );
            }

            for (int i = 0; i < dTableModel.getRows().size(); i++) {
                DTableModel.DRowModel row = dTableModel.getRows().get(i);

                sb.append( "\n" );
                sb.append( "    private static Rule rule_" + clasName + "_" + i + "() {\n" );
                sb.append( "        return D.rule(\"" + pkgName + "\", \"" + clasName + "_" + i + "\")\n" );
                sb.append( "                .unit(" + pkgName + "." + clasName + "DTUnit.class)\n" );
                sb.append( "                .build( \n" );

                for (int j = 0; j < dTableModel.getInputSize(); j++) {
                    sb.append( "                       D.pattern(var_$pattern$" + j + "$).expr(TEST_ARRAY[" + i + "][" + j + "].getName(), var_evaluator,\n" );
                    sb.append( "                           (_this, evaluator) -> TEST_ARRAY[" + i + "][" + j + "].test( evaluator.getEvalCtx(" + j + "), _this.getValue() )),\n" );
                }

                sb.append( "                       D.on( var_evaluator, " );
                sb.append( IntStream.range( 0, dTableModel.getOutputSize() ).mapToObj( j -> "var_output" + j ).collect( joining(", ") ) );
                sb.append( " ).execute(( evaluator, " );
                sb.append( IntStream.range( 0, dTableModel.getOutputSize() ).mapToObj( j -> "output" + j ).collect( joining(", ") ) );
                sb.append( " ) -> {\n" );
                for (int j = 0; j < dTableModel.getOutputSize(); j++) {
                    sb.append( "                            output" + j + ".add(evaluator.getOutput(" + i + ", " + j + "));\n" );
                }
                sb.append( "                            evaluator.registerFire(" + i + ");\n" );

                sb.append( "                       }\n" );
                sb.append( "        ));\n" );
                sb.append( "    }\n" );
            }

            sb.append( "}\n" );

            String source = sb.toString();
            if (logger.isDebugEnabled()) {
                logger.debug( clasName + ":\n" + source );
            }
            return source;
        }
    }

    public static class UnitSourceGenerator implements SourceGenerator {
        public String generate( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel ) {
            String pkgName = dTableModel.getNamespace();
            String clasName = dTableModel.getTableName();

            StringBuilder sb = new StringBuilder();
            sb.append( "package " ).append( pkgName ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "import java.util.List;\n" );
            sb.append( "import java.util.ArrayList;\n" );
            sb.append( "import " ).append( DataSource.class.getCanonicalName() ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "public class " ).append( clasName ).append( "DTUnit extends " + DMNUnit.class.getCanonicalName() + " {\n" );

            for (int i = 0; i < dTableModel.getInputSize(); i++) {
                sb.append( "\n" );
                sb.append( "    private DataSource<Object> input" ).append( i ).append( ";\n" );
                sb.append( "    public DataSource<Object> getInput" ).append( i ).append( "() {\n" );
                sb.append( "        return input" ).append( i ).append( ";\n" );
                sb.append( "    }\n" );
            }

            for (int i = 0; i < dTableModel.getOutputSize(); i++) {
                sb.append( "\n" );
                sb.append( "    private List<Object> output" + i + " = new ArrayList<Object>();\n" );
                sb.append( "    public List<Object> getOutput" + i + "() {\n" );
                sb.append( "        return output" + i + ";\n" );
                sb.append( "    }\n" );
            }

            sb.append( "\n" );
            sb.append( "    @Override\n" );
            sb.append( "    public void onStart() {\n" );
            for (int i = 0; i < dTableModel.getInputSize(); i++) {
                sb.append( "        input" ).append( i ).append( " = DataSource.create( getValue(" ).append( i ).append( ") );\n" );
            }
            sb.append( "    }\n" );

            sb.append( "\n" );
            sb.append( "    @Override\n" );
            sb.append( "    public void onEnd() {\n" );
            sb.append( "        result = applyHitPolicy( " );
            sb.append( IntStream.range( 0, dTableModel.getOutputSize() ).mapToObj( i -> "output" + i ).collect( joining(", ") ) );
            sb.append( " );\n" );
            sb.append( "    }\n" );
            sb.append( "}\n" );

            String source = sb.toString();
            if (logger.isDebugEnabled()) {
                logger.debug( clasName + ":\n" + source );
            }
            return source;
        }
    }

    public static class UnaryTestsSourceGenerator implements SourceGenerator {
        public String generate( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel ) {
            String pkgName = dTableModel.getNamespace();
            String clasName = dTableModel.getTableName();

            StringBuilder sb = new StringBuilder();
            sb.append( "package " ).append( pkgName ).append( ";\n" );
            sb.append( "\n" );
            sb.append( "import java.util.List;\n" );
            sb.append( "import org.kie.dmn.feel.codegen.feel11.CompiledFEELUnaryTests;\n" );
            sb.append( "import org.kie.dmn.feel.codegen.feel11.CompiledFEELSupport;\n" );
            sb.append( "import org.kie.dmn.feel.codegen.feel11.CompiledCustomFEELFunction;\n" );
            sb.append( "import org.kie.dmn.feel.runtime.UnaryTest;\n" );
            sb.append( "import org.kie.dmn.feel.lang.EvaluationContext;\n" );
            sb.append( "import " ).append( CompiledDTTest.class.getCanonicalName() ).append( ";\n" );
            sb.append( "import static org.kie.dmn.feel.codegen.feel11.CompiledFEELSemanticMappings.*;\n" );
            sb.append( "\n" );
            sb.append( "public class " ).append( clasName ).append( "UnaryTests {\n" );
            sb.append( "\n" );
            sb.append( getUnaryTestsSource(ctx, feel, dTableModel, pkgName, clasName) );
            sb.append( "}\n" );

            String source = sb.toString();
            if (logger.isDebugEnabled()) {
                logger.debug( clasName + ":\n" + source );
            }
            return source;
        }

        public String getUnaryTestsSource( DMNCompilerContext ctx, DMNFEELHelper feel, DTableModel dTableModel, String pkgName, String className ) {
            StringBuilder testArrayBuilder = new StringBuilder();
            StringBuilder testsBuilder = new StringBuilder();
            StringBuilder instancesBuilder = new StringBuilder();

            Map<String, String> testClassesByInput = new HashMap<>();
            testArrayBuilder.append( "    public static final CompiledDTTest[][] TEST_ARRAY = new CompiledDTTest[][] {\n" );

            for (int i = 0; i < dTableModel.getRows().size(); i++) {
                testArrayBuilder.append( "            { " );
                DTableModel.DRowModel row = dTableModel.getRows().get(i);
                for (int j = 0; j < row.getInputs().size(); j++) {
                    String input = row.getInputs().get(j);
                    String testClass = testClassesByInput.get(input);
                    if (testClass == null) {
                        testClass = className + "r" + i + "c" + j;
                        testClassesByInput.put(input, testClass);
                        instancesBuilder.append( "    private static final CompiledDTTest " + testClass + "_INSTANCE = new CompiledDTTest( new " + testClass + "() );\n" );

                        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = feel.generateStaticUnaryTestsSource(
                                input,
                                ctx,
                                dTableModel.getColumns().get(j).getType());

                        replaceSimpleNameWith(classOrInterfaceDeclaration, "TemplateCompiledFEELUnaryTests", testClass);
                        String sourceCode = classOrInterfaceDeclaration.setName(testClass).toString();

                        testsBuilder.append( "\n" );
                        testsBuilder.append( sourceCode );
                        testsBuilder.append( "\n" );
                    }
                    testArrayBuilder.append( testClass ).append( "_INSTANCE" );
                    if (j < row.getInputs().size()-1) {
                        testArrayBuilder.append( ", " );
                    }
                }
                if (i < dTableModel.getRows().size()-1) {
                    testArrayBuilder.append( " },\n" );
                } else {
                    testArrayBuilder.append( " }\n" );
                }
            }

            testArrayBuilder.append( "    };\n" );

            return instancesBuilder + "\n" + testArrayBuilder + "\n" + testsBuilder;
        }
    }
}

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

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.BodyDeclaration;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.InitializerDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.comments.JavadocComment;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.Expression;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.WindowReference;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;

public class PackageModel {

    private static final Logger logger          = LoggerFactory.getLogger(PackageModel.class);


    private static final String RULES_FILE_NAME = "Rules";

    private static final int RULES_PER_CLASS = 20;

    private final String name;
    private final String rulesFileName;
    
    private Set<String> imports = new HashSet<>();

    private Map<String, Class<?>> globals = new HashMap<>();

    private Map<String, MethodDeclaration> ruleMethods = new LinkedHashMap<>(); // keep rules order to obey implicit salience

    private Map<String, MethodDeclaration> queryMethods = new HashMap<>();

    private Map<String, QueryGenerator.QueryDefWithType> queryDefWithType = new HashMap<>();

    private Map<String, MethodCallExpr> windowReferences = new HashMap<>();

    private Map<String, List<QueryParameter>> queryVariables = new HashMap<>();

    private List<MethodDeclaration> functions = new ArrayList<>();

    private List<ClassOrInterfaceDeclaration> generatedPOJOs = new ArrayList<>();

    private List<Expression> typeMetaDataExpressions = new ArrayList<>();

    private DRLIdGenerator exprIdGenerator;

    private KnowledgeBuilderConfigurationImpl configuration;
    private Map<String, AccumulateFunction> accumulateFunctions;


    public PackageModel(String name, KnowledgeBuilderConfigurationImpl configuration) {
        this.name = name;
        this.rulesFileName = generateRulesFileName();
        this.configuration = configuration;
        exprIdGenerator = new DRLIdGenerator();
    }

    public String getRulesFileName() {
        return rulesFileName;
    }

    private String generateRulesFileName() {
        return RULES_FILE_NAME + generateUUID();
    }

    public KnowledgeBuilderConfigurationImpl getConfiguration() {
        return configuration;
    }

    public String getName() {
        return name;
    }
    
    public DRLIdGenerator getExprIdGenerator() {
        return exprIdGenerator;
    }

    public void addImports(Collection<String> imports) {
        this.imports.addAll(imports);
    }

    public Collection<String> getImports() {
        return this.imports;
    }

    public void addGlobals(Map<String, String> values) {
        Map<String, Class<?>> transformed;
        transformed = values
                .entrySet()
                .stream()
                .collect(Collectors.toMap( Entry::getKey, e -> {
                    try {
                        return Class.forName(e.getValue());
                    } catch (ClassNotFoundException e1) {
                        throw new UnsupportedOperationException("Class not found", e1);
                    }
                }));
        globals.putAll(transformed);
    }

    public Map<String, Class<?>> getGlobals() {
        return globals;
    }

    public void addTypeMetaDataExpressions(Expression typeMetaDataExpression) {
        typeMetaDataExpressions.add(typeMetaDataExpression);
    }

    public void putRuleMethod(String methodName, MethodDeclaration ruleMethod) {
        this.ruleMethods.put(methodName, ruleMethod);
    }

    public void putQueryMethod(MethodDeclaration queryMethod) {
        this.queryMethods.put(queryMethod.getNameAsString(), queryMethod);
    }

    public MethodDeclaration getQueryMethod(String key) {
        return queryMethods.get(key);
    }

    public void putQueryVariable(String queryName, QueryParameter qp) {
        this.queryVariables.computeIfAbsent(queryName, k -> new ArrayList<>());
        this.queryVariables.get(queryName).add(qp);
    }

    public List<QueryParameter> queryVariables(String queryName) {
        return this.queryVariables.get(queryName);
    }

    public Map<String, QueryGenerator.QueryDefWithType> getQueryDefWithType() {
        return queryDefWithType;
    }

    public void addAllFunctions(List<MethodDeclaration> functions) {
        this.functions.addAll(functions);
    }

    public void addGeneratedPOJO(ClassOrInterfaceDeclaration pojo) {
        this.generatedPOJOs.add(pojo);
    }

    public List<ClassOrInterfaceDeclaration> getGeneratedPOJOsSource() {
        return generatedPOJOs;
    }

    public void addAllWindowReferences(String methodName, MethodCallExpr windowMethod) {
        this.windowReferences.put(methodName, windowMethod);
    }

    public Map<String, MethodCallExpr> getWindowReferences() {
        return windowReferences;
    }

    final static Type WINDOW_REFERENCE_TYPE = JavaParser.parseType(WindowReference.class.getCanonicalName());

    public List<MethodDeclaration> getFunctions() {
        return functions;
    }

    public Map<String, AccumulateFunction> getAccumulateFunctions() {
        return accumulateFunctions;
    }

    public static class RuleSourceResult {

        private final CompilationUnit mainRuleClass;
        private Collection<CompilationUnit> splitted = new ArrayList<>();

        public RuleSourceResult(CompilationUnit mainRuleClass) {
            this.mainRuleClass = mainRuleClass;
        }

        public CompilationUnit getMainRuleClass() {
            return mainRuleClass;
        }

        /**
         * Append additional class to source results.
         * @param additionalCU 
         */
        public RuleSourceResult with(CompilationUnit additionalCU) {
            splitted.add(additionalCU);
            return this;
        }

        public Collection<CompilationUnit> getSplitted() {
            return Collections.unmodifiableCollection(splitted);
        }

    }

    public RuleSourceResult getRulesSource() {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( name );

        manageImportForCompilationUnit(cu);
        
        ClassOrInterfaceDeclaration rulesClass = cu.addClass(rulesFileName);
        rulesClass.addImplementedType(Model.class);

        BodyDeclaration<?> dateFormatter = JavaParser.parseBodyDeclaration(
                "public final static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.getDateFormatMask());\n");
        rulesClass.addMember(dateFormatter);

        BodyDeclaration<?> getNameMethod = JavaParser.parseBodyDeclaration(
                "    @Override\n" +
                "        public String getName() {\n" +
                "        return \"" + name + "\";\n" +
                "    }\n"
                );
        rulesClass.addMember(getNameMethod);

        BodyDeclaration<?> getRulesMethod = JavaParser.parseBodyDeclaration(
                "    @Override\n" +
                "    public List<Rule> getRules() {\n" +
                "        return rules;\n" +
                "    }\n"
                );
        rulesClass.addMember(getRulesMethod);

        StringBuilder sb = new StringBuilder("\n");
        sb.append("With the following expression ID:\n");
        sb.append(exprIdGenerator.toString());
        sb.append("\n");
        JavadocComment exprIdComment = new JavadocComment(sb.toString());
        getRulesMethod.setComment(exprIdComment);

        BodyDeclaration<?> getGlobalsMethod = JavaParser.parseBodyDeclaration(
                "    @Override\n" +
                "    public List<Global> getGlobals() {\n" +
                "        return globals;\n" +
                "    }\n");
        rulesClass.addMember(getGlobalsMethod);

        BodyDeclaration<?> getQueriesMethod = JavaParser.parseBodyDeclaration(
                "    @Override\n" +
                "    public List<Query> getQueries() {\n" +
                "        return queries;\n" +
                "    }\n");
        rulesClass.addMember(getQueriesMethod);

        BodyDeclaration<?> getTypeMetaDataMethod = JavaParser.parseBodyDeclaration(
                "    @Override\n" +
                "    public List<TypeMetaData> getTypeMetaDatas() {\n" +
                "        return typeMetaDatas;\n" +
                "    }\n");
        rulesClass.addMember(getTypeMetaDataMethod);

        BodyDeclaration<?> rulesList = JavaParser.parseBodyDeclaration("List<Rule> rules = new ArrayList<>();");
        rulesClass.addMember(rulesList);
        BodyDeclaration<?> queriesList = JavaParser.parseBodyDeclaration("List<Query> queries = new ArrayList<>();");
        rulesClass.addMember(queriesList);
        BodyDeclaration<?> globalsList = JavaParser.parseBodyDeclaration("List<Global> globals = new ArrayList<>();");
        rulesClass.addMember(globalsList);
        BodyDeclaration<?> windowReferencesList = JavaParser.parseBodyDeclaration("List<WindowReference> windowReferences = new ArrayList<>();");
        rulesClass.addMember(windowReferencesList);
        BodyDeclaration<?> typeMetaDatasList = JavaParser.parseBodyDeclaration("List<TypeMetaData> typeMetaDatas = new ArrayList<>();");
        rulesClass.addMember(typeMetaDatasList);
        // end of fixed part


        for(Map.Entry<String, MethodCallExpr> windowReference : windowReferences.entrySet()) {
            FieldDeclaration f = rulesClass.addField(WINDOW_REFERENCE_TYPE, windowReference.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            f.getVariables().get(0).setInitializer(windowReference.getValue());
        }

        for ( Map.Entry<String, Class<?>> g : getGlobals().entrySet() ) {
            addGlobalField(rulesClass, getName(), g.getKey(), g.getValue());
        }

        for(Map.Entry<String, QueryGenerator.QueryDefWithType> queryDef: queryDefWithType.entrySet()) {
            FieldDeclaration field = rulesClass.addField(queryDef.getValue().getQueryType(), queryDef.getKey(), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
            field.getVariables().get(0).setInitializer(queryDef.getValue().getMethodCallExpr());
        }

        for(Map.Entry<String, MethodDeclaration> methodName: queryMethods.entrySet()) {
            FieldDeclaration field = rulesClass.addField(methodName.getValue().getType(), methodName.getKey(), Modifier.FINAL);
            field.getVariables().get(0).setInitializer(new MethodCallExpr(null, methodName.getKey()));
        }

        // instance initializer block.
        // add to `rules` list the result of invoking each method for rule 
        InitializerDeclaration rulesListInitializer = new InitializerDeclaration();
        rulesClass.addMember(rulesListInitializer);
        BlockStmt rulesListInitializerBody = new BlockStmt();
        rulesListInitializer.setBody(rulesListInitializerBody);

        for ( String methodName : queryMethods.keySet() ) {
            NameExpr rulesFieldName = new NameExpr( "queries" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new NameExpr(methodName) );
            rulesListInitializerBody.addStatement( add );
        }

        for ( String fieldName : windowReferences.keySet() ) {
            NameExpr rulesFieldName = new NameExpr( "windowReferences" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new NameExpr(fieldName) );
            rulesListInitializerBody.addStatement( add );
        }

        for ( Map.Entry<String, Class<?>> g : getGlobals().entrySet() ) {
            NameExpr rulesFieldName = new NameExpr( "globals" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new NameExpr(toVar(g.getKey())) );
            rulesListInitializerBody.addStatement( add );
        }

        for (Expression expr : typeMetaDataExpressions) {
            NameExpr rulesFieldName = new NameExpr( "typeMetaDatas" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( expr );
            rulesListInitializerBody.addStatement( add );
        }

        functions.forEach(rulesClass::addMember);

        RuleSourceResult results = new RuleSourceResult(cu);

        // each method per Drlx parser result
        int count = -1;
        Map<Integer, ClassOrInterfaceDeclaration> splitted = new LinkedHashMap<>();
        for (Entry<String, MethodDeclaration> ruleMethodKV : ruleMethods.entrySet()) {
            ClassOrInterfaceDeclaration rulesMethodClass = splitted.computeIfAbsent(++count / RULES_PER_CLASS, i -> {
                CompilationUnit cuRulesMethod = new CompilationUnit();
                results.with(cuRulesMethod);
                cuRulesMethod.setPackageDeclaration(name);
                manageImportForCompilationUnit(cuRulesMethod);
                cuRulesMethod.addImport(JavaParser.parseImport("import static " + name + "." + rulesFileName + ".*;"));
                String currentRulesMethodClassName = rulesFileName + "RuleMethods" + i;
                return cuRulesMethod.addClass(currentRulesMethodClassName);
            });
            rulesMethodClass.addMember(ruleMethodKV.getValue());

            // manage in main class init block:
            NameExpr rulesFieldName = new NameExpr("rules");
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument(new MethodCallExpr(new NameExpr(rulesMethodClass.getNameAsString()), ruleMethodKV.getKey()));
            rulesListInitializerBody.addStatement(add);
        }

        queryMethods.values().forEach(rulesClass::addMember);
        

        return results;
    }

    private void manageImportForCompilationUnit(CompilationUnit cu) {
        // fixed part
        cu.addImport(JavaParser.parseImport("import java.util.*;"                          ));
        cu.addImport(JavaParser.parseImport("import org.drools.model.*;"                   ));
        cu.addImport(JavaParser.parseImport("import static org.drools.model.DSL.*;"        ));
        cu.addImport(JavaParser.parseImport("import org.drools.model.Index.ConstraintType;"));
        cu.addImport(JavaParser.parseImport("import java.time.*;"));
        cu.addImport(JavaParser.parseImport("import java.time.format.*;"));
        cu.addImport(JavaParser.parseImport("import java.text.*;"));
        cu.addImport(JavaParser.parseImport("import org.drools.core.util.*;"));

        // imports from DRL:
        for ( String i : imports ) {
            if ( i.equals(name+".*") ) {
                continue; // skip same-package star import.
            }
            cu.addImport(JavaParser.parseImport("import "+i+";"));
        }
    }

    private static void addGlobalField(ClassOrInterfaceDeclaration classDeclaration, String packageName, String globalName, Class<?> globalClass) {
        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Global.class.getCanonicalName());
        varType.setTypeArguments(DrlxParseUtil.classToReferenceType(globalClass));
        Type declType = DrlxParseUtil.classToReferenceType(globalClass);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, "globalOf");
        declarationOfCall.addArgument(new ClassExpr(declType ));
        declarationOfCall.addArgument(new StringLiteralExpr(packageName));
        declarationOfCall.addArgument(new StringLiteralExpr(globalName));

        FieldDeclaration field = classDeclaration.addField(varType, toVar(globalName), Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);

        field.getVariables().get(0).setInitializer(declarationOfCall);
    }

    public void logRule(String source) {
        logger.debug("=====");
        logger.debug(source);
        logger.debug("=====");
    }

    public void addAccumulateFunctions(Map<String, AccumulateFunction> accumulateFunctions) {
        this.accumulateFunctions = accumulateFunctions;
    }

    public boolean hasDeclaration(String id) {
        return globals.get(id) != null;
    }
}

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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.TypeDeclarationUtils;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.EntryPointDeclarationDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.factmodel.ClassDefinition;
import org.drools.model.DomainClassMetadata;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.model.Query;
import org.drools.model.Rule;
import org.drools.model.RulesSupplier;
import org.drools.model.WindowReference;
import org.drools.model.functions.PredicateInformation;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.DrlxParseUtil;
import org.drools.modelcompiler.builder.generator.QueryGenerator;
import org.drools.modelcompiler.builder.generator.QueryParameter;
import org.drools.modelcompiler.builder.generator.TypedExpression;
import org.drools.modelcompiler.util.lambdareplace.CreatedClass;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.internal.ruleunit.RuleUnitDescription;

import static com.github.javaparser.StaticJavaParser.parseBodyDeclaration;
import static com.github.javaparser.ast.Modifier.finalModifier;
import static com.github.javaparser.ast.Modifier.publicModifier;
import static com.github.javaparser.ast.Modifier.staticModifier;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.drools.core.impl.StatefulKnowledgeSessionImpl.DEFAULT_RULE_UNIT;
import static org.drools.core.util.StringUtils.getPkgUUID;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toClassOrInterfaceType;
import static org.drools.modelcompiler.builder.generator.DrlxParseUtil.toVar;
import static org.drools.modelcompiler.builder.generator.DslMethodNames.GLOBAL_OF_CALL;
import static org.drools.modelcompiler.builder.generator.QueryGenerator.QUERY_METHOD_PREFIX;
import static org.drools.modelcompiler.util.ClassUtil.asJavaSourceName;
import static org.drools.modelcompiler.util.ClassUtil.getAccessibleProperties;

public class PackageModel {

    public static final String DATE_TIME_FORMATTER_FIELD = "DATE_TIME_FORMATTER";

    private static final String RULES_FILE_NAME = "Rules";

    public static final String DOMAIN_CLASSESS_METADATA_FILE_NAME = "DomainClassesMetadata";
    public static final String DOMAIN_CLASS_METADATA_INSTANCE = "_Metadata_INSTANCE";

    private static final int RULES_DECLARATION_PER_CLASS = 1000;

    private final String name;
    private final DialectCompiletimeRegistry dialectCompiletimeRegistry;

    private final String rulesFileName;
    
    private final Set<String> imports = new HashSet<>();
    private final Set<String> staticImports = new HashSet<>();
    private final Set<String> entryPoints = new HashSet<>();
    private Map<String, Method> staticMethods;

    private final Map<String, Class<?>> globals = new HashMap<>();

    private final Map<String, Map<Integer, MethodDeclaration>> ruleMethods = new ConcurrentHashMap<>();

    private final Set<String> queryNames = new HashSet<>();
    private final Map<String, MethodDeclaration> queryMethods = new ConcurrentHashMap<>();
    private final Map<String, Set<QueryModel>> queriesByRuleUnit = new ConcurrentHashMap<>();

    private final Map<String, QueryGenerator.QueryDefWithType> queryDefWithType = new HashMap<>();

    private final Map<String, MethodCallExpr> windowReferences = new HashMap<>();

    private final Map<String, List<QueryParameter>> queryVariables = new HashMap<>();

    private final List<MethodDeclaration> functions = new ArrayList<>();

    private final List<TypeDeclaration> generatedPOJOs = new ArrayList<>();
    private final List<GeneratedClassWithPackage> generatedAccumulateClasses = new ArrayList<>();

    private final Set<Class<?>> domainClasses = new HashSet<>();
    private final Map<Class<?>, ClassDefinition> classDefinitionsMap = new HashMap<>();

    private final List<Expression> typeMetaDataExpressions = new ArrayList<>();

    private final DRLIdGenerator exprIdGenerator;

    private final KnowledgeBuilderConfigurationImpl configuration;
    private Map<String, AccumulateFunction> accumulateFunctions;
    private InternalKnowledgePackage pkg;

    private final String pkgUUID;
    private final Set<RuleUnitDescription> ruleUnits = Collections.synchronizedSet( new HashSet<>() );

    private final Map<LambdaExpr, java.lang.reflect.Type> lambdaReturnTypes = new ConcurrentHashMap<>();
    private final Map<String, PredicateInformation> allConstraintsMap = new ConcurrentHashMap<>();
    private final Map<String, TypedExpression> dateFields = new ConcurrentHashMap<>();

    private final Map<String, CreatedClass> lambdaClasses = new ConcurrentHashMap<>();

    private boolean oneClassPerRule;

    public PackageModel( ReleaseId releaseId, String name, KnowledgeBuilderConfigurationImpl configuration, DialectCompiletimeRegistry dialectCompiletimeRegistry, DRLIdGenerator exprIdGenerator) {
        this(name, configuration, dialectCompiletimeRegistry, exprIdGenerator, getPkgUUID(releaseId, name));
    }

    public PackageModel(String gav, String name, KnowledgeBuilderConfigurationImpl configuration, DialectCompiletimeRegistry dialectCompiletimeRegistry, DRLIdGenerator exprIdGenerator) {
        this(name, configuration, dialectCompiletimeRegistry, exprIdGenerator, getPkgUUID(gav, name));
    }

    public PackageModel(String name, KnowledgeBuilderConfigurationImpl configuration, DialectCompiletimeRegistry dialectCompiletimeRegistry, DRLIdGenerator exprIdGenerator, String pkgUUID) {
        this.name = name;
        this.pkgUUID = pkgUUID;
        this.rulesFileName = RULES_FILE_NAME + pkgUUID;
        this.configuration = configuration;
        this.exprIdGenerator = exprIdGenerator;
        this.dialectCompiletimeRegistry = dialectCompiletimeRegistry;
    }

    public Map<String, CreatedClass> getLambdaClasses() {
        return lambdaClasses;
    }

    public boolean isOneClassPerRule() {
        return oneClassPerRule;
    }

    public void setOneClassPerRule( boolean oneClassPerRule ) {
        this.oneClassPerRule = oneClassPerRule;
    }

    public String getPackageUUID() {
        return pkgUUID;
    }

    public String getDomainClassName( Class<?> clazz ) {
        return DOMAIN_CLASSESS_METADATA_FILE_NAME + getPackageUUID() + "." + asJavaSourceName( clazz ) + DOMAIN_CLASS_METADATA_INSTANCE;
    }

    public String getRulesFileName() {
        return rulesFileName;
    }

    public KnowledgeBuilderConfigurationImpl getConfiguration() {
        return configuration;
    }

    public String getName() {
        return name;
    }

    public String getPathName() {
        return name.replace('.', '/');
    }

    public String getRulesFileNameWithPackage() {
        return name + "." + rulesFileName;
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

    public void addStaticImports(Collection<String> imports) {
        this.staticImports.addAll(imports);
    }

    public void addEntryPoints(Collection<EntryPointDeclarationDescr> entryPoints) {
        entryPoints.stream().map( EntryPointDeclarationDescr::getEntryPointId ).forEach( this.entryPoints::add );
    }

    public void addEntryPoint(String name) {
        entryPoints.add( name );
    }

    public boolean hasEntryPoint(String name) {
        return entryPoints.contains( name );
    }

    public Collection<String> getStaticImports() {
        return this.staticImports;
    }

    public Method getStaticMethod(String methodName) {
        return getStaticMethods().get(methodName);
    }

    public void addDateField(String fieldName, TypedExpression expression) {
        dateFields.put( fieldName, expression );
    }

    public Map<String, Method> getStaticMethods() {
        if (staticMethods == null) {
            Map<String, Method> methodsMap = new HashMap<>();
            for (String i : staticImports) {
                if (i.endsWith( ".*" )) {
                    String className = i.substring( 0, i.length()-2 );
                    try {
                        Class<?> importedClass = pkg.getTypeResolver().resolveType( className );
                        for (Method m : importedClass.getMethods()) {
                            if (java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                                methodsMap.put(m.getName(), m);
                            }
                        }
                    } catch (ClassNotFoundException e1) {
                        throw new UnsupportedOperationException("Class not found", e1);
                    }
                } else {
                    int splitPoint = i.lastIndexOf( '.' );
                    String className = i.substring( 0, splitPoint );
                    String methodName = i.substring( splitPoint+1 );
                    try {
                        Class<?> importedClass = pkg.getTypeResolver().resolveType( className );
                        for (Method m : importedClass.getMethods()) {
                            if (java.lang.reflect.Modifier.isStatic(m.getModifiers()) && m.getName().equals( methodName )) {
                                methodsMap.put(methodName, m);
                                break;
                            }
                        }
                    } catch (ClassNotFoundException e1) {
                        throw new UnsupportedOperationException("Class not found", e1);
                    }
                }
            }
            staticMethods = methodsMap;
        }
        return staticMethods;
    }

    public void addGlobals(InternalKnowledgePackage pkg) {
        globals.putAll( pkg.getGlobals() );
    }

    public void addGlobal(String name, Class<?> type) {
        globals.put( name, type );
    }

    public Map<String, Class<?>> getGlobals() {
        return globals;
    }

    public void addTypeMetaDataExpressions(Expression typeMetaDataExpression) {
        typeMetaDataExpressions.add(typeMetaDataExpression);
    }

    public void putRuleMethod(String unitName, MethodDeclaration ruleMethod, int ruleIndex) {
        ruleMethods.computeIfAbsent(unitName, k -> Collections.synchronizedMap( new TreeMap<>() )).put( ruleIndex, ruleMethod );
    }

    public void putRuleUnit(String unitName) {
        ruleMethods.computeIfAbsent(unitName, k -> Collections.synchronizedMap( new TreeMap<>() ));
    }

    public void putQueryMethod(MethodDeclaration queryMethod) {
        this.queryMethods.put(queryMethod.getNameAsString(), queryMethod);
    }

    public void registerQueryName(String queryName) {
        queryNames.add(queryName);
    }

    public boolean hasQuery(String queryName) {
        return queryNames.contains(queryName);
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

    public void addGeneratedPOJO(TypeDeclaration pojo) {
        this.generatedPOJOs.add(pojo);
    }

    public List<TypeDeclaration> getGeneratedPOJOsSource() {
        return generatedPOJOs;
    }

    public void addGeneratedAccumulateClasses(GeneratedClassWithPackage clazz) {
        this.generatedAccumulateClasses.add(clazz);
    }

    public List<GeneratedClassWithPackage> getGeneratedAccumulateClasses() {
        return generatedAccumulateClasses;
    }

    public void addAllWindowReferences(String methodName, MethodCallExpr windowMethod) {
        this.windowReferences.put(methodName, windowMethod);
    }

    public Map<String, MethodCallExpr> getWindowReferences() {
        return windowReferences;
    }

    final static Type WINDOW_REFERENCE_TYPE = toClassOrInterfaceType(WindowReference.class);

    public List<MethodDeclaration> getFunctions() {
        return functions;
    }

    public Map<String, AccumulateFunction> getAccumulateFunctions() {
        return accumulateFunctions;
    }

    public void setInternalKnowledgePackage(InternalKnowledgePackage pkg) {
        this.pkg = pkg;
    }

    public InternalKnowledgePackage getPkg() {
        return pkg;
    }


    public DialectCompiletimeRegistry getDialectCompiletimeRegistry() {
        return dialectCompiletimeRegistry;
    }

    public void addRuleUnit(RuleUnitDescription ruleUnitDescription) {
        this.ruleUnits.add(ruleUnitDescription);
    }

    public Collection<RuleUnitDescription> getRuleUnits() {
        return ruleUnits;
    }

    public void addQueryInRuleUnit(RuleUnitDescription ruleUnitDescription, QueryModel query) {
        addRuleUnit(ruleUnitDescription);
        queriesByRuleUnit.computeIfAbsent( ruleUnitDescription.getSimpleName(), k -> Collections.synchronizedSet( new HashSet<>() ) ).add(query);
    }

    public Collection<QueryModel> getQueriesInRuleUnit(Class<?> ruleUnitType) {
        String simpleName = ruleUnitType.getSimpleName();
        return getQueriesInRuleUnit(simpleName);
    }

    public Collection<QueryModel> getQueriesInRuleUnit(RuleUnitDescription ruleUnitDescription) {
        String simpleName = ruleUnitDescription.getSimpleName();
        return getQueriesInRuleUnit(simpleName);
    }

    private Collection<QueryModel> getQueriesInRuleUnit(String simpleName) {
        return queriesByRuleUnit.getOrDefault(simpleName, Collections.emptySet() );
    }

    public static class RuleSourceResult {

        private final CompilationUnit mainRuleClass;
        private Collection<CompilationUnit> modelClasses = new ArrayList<>();
        private Map<String, String> modelsByUnit = new HashMap<>();

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
        public RuleSourceResult withClass( CompilationUnit additionalCU ) {
            modelClasses.add(additionalCU);
            return this;
        }

        public RuleSourceResult withModel( String unit, String model ) {
            modelsByUnit.put(unit, model);
            return this;
        }

        public Collection<CompilationUnit> getModelClasses() {
            return Collections.unmodifiableCollection( modelClasses );
        }

        public Map<String, String> getModelsByUnit() {
            return modelsByUnit;
        }
    }

    public RuleSourceResult getRulesSource() {
        boolean hasRuleUnit = !ruleUnits.isEmpty();
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( name );

        manageImportForCompilationUnit(cu);

        ClassOrInterfaceDeclaration rulesClass = cu.addClass(rulesFileName);
        rulesClass.addImplementedType(Model.class.getCanonicalName());
        if (hasRuleUnit) {
            rulesClass.addModifier( Modifier.Keyword.ABSTRACT );
        }

        BodyDeclaration<?> dateFormatter = parseBodyDeclaration(
                "public final static java.time.format.DateTimeFormatter " + DATE_TIME_FORMATTER_FIELD +
                        " = new java.time.format.DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern(org.drools.core.util.DateUtils.getDateFormatMask()).toFormatter(java.util.Locale.ENGLISH);\n");
        rulesClass.addMember(dateFormatter);

        BodyDeclaration<?> getNameMethod = parseBodyDeclaration(
                "    @Override\n" +
                "    public String getName() {\n" +
                "        return \"" + name + "\";\n" +
                "    }\n"
                );
        rulesClass.addMember(getNameMethod);

        String entryPointsBuilder = entryPoints.isEmpty() ?
                "java.util.Collections.emptyList()" :
                "java.util.Arrays.asList(D.entryPoint(\"" + entryPoints.stream().collect( joining("\"), D.entryPoint(\"") ) + "\"))";

        BodyDeclaration<?> getEntryPointsMethod = parseBodyDeclaration(
                "    @Override\n" +
                "    public java.util.List<org.drools.model.EntryPoint> getEntryPoints() {\n" +
                "        return " + entryPointsBuilder + ";\n" +
                "    }\n"
                );
        rulesClass.addMember(getEntryPointsMethod);

        BodyDeclaration<?> getGlobalsMethod = parseBodyDeclaration(
                "    @Override\n" +
                "    public java.util.List<org.drools.model.Global> getGlobals() {\n" +
                "        return globals;\n" +
                "    }\n");
        rulesClass.addMember(getGlobalsMethod);

        BodyDeclaration<?> getTypeMetaDataMethod = parseBodyDeclaration(
                "    @Override\n" +
                "    public java.util.List<org.drools.model.TypeMetaData> getTypeMetaDatas() {\n" +
                "        return typeMetaDatas;\n" +
                "    }\n");
        rulesClass.addMember(getTypeMetaDataMethod);

        // end of fixed part

        for ( Map.Entry<String, TypedExpression> dateField : dateFields.entrySet() ) {
            FieldDeclaration f = rulesClass.addField(dateField.getValue().getJPType(), dateField.getKey(), publicModifier().getKeyword(), staticModifier().getKeyword(), finalModifier().getKeyword());
            f.getVariables().get(0).setInitializer(dateField.getValue().getExpression());
        }

        for ( Map.Entry<String, MethodCallExpr> windowReference : windowReferences.entrySet() ) {
            FieldDeclaration f = rulesClass.addField(WINDOW_REFERENCE_TYPE, windowReference.getKey(), publicModifier().getKeyword(), staticModifier().getKeyword(), finalModifier().getKeyword());
            f.getVariables().get(0).setInitializer(windowReference.getValue());
        }

        for ( Map.Entry<String, Class<?>> g : getGlobals().entrySet() ) {
            addGlobalField(rulesClass, getName(), g.getKey(), g.getValue());
        }

        for(Map.Entry<String, QueryGenerator.QueryDefWithType> queryDef: queryDefWithType.entrySet()) {
            FieldDeclaration field = rulesClass.addField(queryDef.getValue().getQueryType(), queryDef.getKey(), publicModifier().getKeyword(), staticModifier().getKeyword(), finalModifier().getKeyword());
            field.getVariables().get(0).setInitializer(queryDef.getValue().getMethodCallExpr());
        }

        // instance initializer block.
        // add to `rules` list the result of invoking each method for rule
        InitializerDeclaration rulesListInitializer = new InitializerDeclaration();
        BlockStmt rulesListInitializerBody = new BlockStmt();
        rulesListInitializer.setBody(rulesListInitializerBody);

        buildArtifactsDeclaration( getGlobals().keySet(), rulesClass, rulesListInitializerBody, "org.drools.model.Global", "globals", true );

        if ( !typeMetaDataExpressions.isEmpty() ) {
            BodyDeclaration<?> typeMetaDatasList = parseBodyDeclaration("java.util.List<org.drools.model.TypeMetaData> typeMetaDatas = new java.util.ArrayList<>();");
            rulesClass.addMember(typeMetaDatasList);
            for (Expression expr : typeMetaDataExpressions) {
                addInitStatement( rulesListInitializerBody, expr, "typeMetaDatas" );
            }
        } else {
            BodyDeclaration<?> typeMetaDatasList = parseBodyDeclaration("java.util.List<org.drools.model.TypeMetaData> typeMetaDatas = java.util.Collections.emptyList();");
            rulesClass.addMember(typeMetaDatasList);
        }

        functions.forEach(rulesClass::addMember);

        RuleSourceResult results = new RuleSourceResult(cu);

        if (hasRuleUnit) {
            ruleMethods.keySet().forEach( unitName -> {
                String className = rulesFileName + "_" + unitName;
                ClassOrInterfaceDeclaration unitClass = createClass( className, results);
                unitClass.addExtendedType( rulesFileName );

                InitializerDeclaration unitInitializer = new InitializerDeclaration();
                BlockStmt unitInitializerBody = new BlockStmt();
                unitInitializer.setBody(unitInitializerBody);

                generateRulesInUnit( unitName, unitInitializerBody, results, unitClass );

                Set<QueryModel> queries = queriesByRuleUnit.get( unitName );
                Collection<String> queryNames = queries == null ? Collections.emptyList() : queries.stream()
                        .map( QueryModel::getName )
                        .map( name -> QUERY_METHOD_PREFIX + name )
                        .collect( toList() );
                Collection<MethodDeclaration> queryImpls = queryNames.stream().map( queryMethods::get ).collect( toList() );
                generateQueriesInUnit( unitClass, unitInitializerBody, queryNames, queryImpls );

                if (!unitInitializerBody.getStatements().isEmpty()) {
                    unitClass.addMember( unitInitializer );
                }
           } );

        } else {
            generateRulesInUnit( DEFAULT_RULE_UNIT, rulesListInitializerBody, results, rulesClass );
            generateQueriesInUnit( rulesClass, rulesListInitializerBody, queryMethods.keySet(), queryMethods.values() );
        }

        if (!rulesListInitializerBody.getStatements().isEmpty()) {
            rulesClass.addMember( rulesListInitializer );
        }

        return results;
    }

    private void generateQueriesInUnit( ClassOrInterfaceDeclaration rulesClass, BlockStmt initializerBody, Collection<String> queryNames, Collection<MethodDeclaration> queryImpls ) {
        if (queryNames == null || queryNames.isEmpty()) {
            BodyDeclaration<?> getQueriesMethod = parseBodyDeclaration(
                    "    @Override\n" +
                    "    public java.util.List<org.drools.model.Query> getQueries() {\n" +
                    "        return java.util.Collections.emptyList();\n" +
                    "    }\n");
            rulesClass.addMember(getQueriesMethod);
            return;
        }

        for (String queryName : queryNames) {
            FieldDeclaration field = rulesClass.addField(Query.class, queryName, finalModifier().getKeyword());
            field.getVariables().get(0).setInitializer(new MethodCallExpr(null, queryName));
        }

        BodyDeclaration<?> getQueriesMethod = parseBodyDeclaration(
                "    @Override\n" +
                "    public java.util.List<org.drools.model.Query> getQueries() {\n" +
                "        return queries;\n" +
                "    }\n");
        rulesClass.addMember(getQueriesMethod);

        queryImpls.forEach(rulesClass::addMember);
        buildArtifactsDeclaration( queryNames, rulesClass, initializerBody, "org.drools.model.Query", "queries", false );
    }

    private void generateRulesInUnit( String ruleUnitName, BlockStmt rulesListInitializerBody, RuleSourceResult results,
                                      ClassOrInterfaceDeclaration rulesClass ) {

        results.withModel( name + "." + ruleUnitName, name + "." + rulesClass.getNameAsString() );

        Collection<MethodDeclaration> ruleMethodsInUnit = ofNullable(ruleMethods.get(ruleUnitName)).map(Map::values).orElse(null);
        if (ruleMethodsInUnit == null || ruleMethodsInUnit.isEmpty()) {
            BodyDeclaration<?> getQueriesMethod = parseBodyDeclaration(
                    "    @Override\n" +
                    "    public java.util.List<org.drools.model.Rule> getRules() {\n" +
                    "        return java.util.Collections.emptyList();\n" +
                    "    }\n");
            rulesClass.addMember(getQueriesMethod);
            return;
        }

        if (!ruleUnitName.equals( DEFAULT_RULE_UNIT )) {
            BodyDeclaration<?> modelNameMethod = parseBodyDeclaration(
                    "    @Override\n" +
                            "    public String getName() {\n" +
                            "        return super.getName() + \"." + ruleUnitName + "\";\n" +
                            "    }\n" );
            rulesClass.addMember( modelNameMethod );

            BodyDeclaration<?> modelPackageNameMethod = parseBodyDeclaration(
                    "    @Override\n" +
                            "    public String getPackageName() {\n" +
                            "        return super.getName();\n" +
                            "    }\n" );
            rulesClass.addMember( modelPackageNameMethod );
        }

        createAndAddGetRulesMethod( rulesClass );

        int ruleCount = ruleMethodsInUnit.size();
        boolean requiresMultipleRulesLists = ruleCount >= RULES_DECLARATION_PER_CLASS-1;
        boolean parallelRulesLoad = ruleCount >= (RULES_DECLARATION_PER_CLASS*3-1);
        MethodCallExpr parallelRulesGetter = null;


        MethodCallExpr rules = buildRulesField( rulesClass );
        if (requiresMultipleRulesLists) {
            rulesClass.addImplementedType(RulesSupplier.class);
            if (parallelRulesLoad) {
                parallelRulesGetter = new MethodCallExpr( new NameExpr( RulesSupplier.class.getCanonicalName() ), "getRules" );
                parallelRulesGetter.addArgument( new ThisExpr() );
                rulesListInitializerBody.addStatement( new AssignExpr( new NameExpr( "this.rules" ), parallelRulesGetter, AssignExpr.Operator.ASSIGN) );
            } else {
                MethodCallExpr add = new MethodCallExpr( new NameExpr( "rules" ), "addAll" );
                add.addArgument( "getRulesList()" );
                rulesListInitializerBody.addStatement( add );
            }
        }

        ruleMethodsInUnit.parallelStream().forEach( DrlxParseUtil::transformDrlNameExprToNameExpr);

        int maxLength = ruleMethodsInUnit
                .parallelStream()
                .map( MethodDeclaration::toString ).mapToInt( String::length ).max().orElse( 1 );
        int rulesPerClass = oneClassPerRule ? 1 : Math.max( 50000 / maxLength, 1 );

        // each method per Drlx parser result
        int count = -1;
        Map<Integer, ClassOrInterfaceDeclaration> splitted = new LinkedHashMap<>();
        for (MethodDeclaration ruleMethod : ruleMethodsInUnit) {
            String methodName = ruleMethod.getNameAsString();
            ClassOrInterfaceDeclaration rulesMethodClass = splitted.computeIfAbsent(++count / rulesPerClass, i -> {
                String className = rulesClass.getNameAsString() + (oneClassPerRule ? "_" + methodName : "RuleMethods" + i);
                return createClass( className, results );
            });
            rulesMethodClass.addMember(ruleMethod);

            if (count % RULES_DECLARATION_PER_CLASS == RULES_DECLARATION_PER_CLASS-1) {
                int index = count / RULES_DECLARATION_PER_CLASS;
                rules = buildRulesField(results, index);

                ObjectCreationExpr newObject = new ObjectCreationExpr(null, toClassOrInterfaceType(rulesFileName + "Rules" + index), NodeList.nodeList());

                if (parallelRulesLoad) {
                    parallelRulesGetter.addArgument( newObject );
                } else {
                    MethodCallExpr add = new MethodCallExpr( new NameExpr( "rules" ), "addAll" );
                    add.addArgument( new MethodCallExpr( newObject, "getRulesList" ) );
                    rulesListInitializerBody.addStatement( add );
                }
            }

            // manage in main class init block:
            rules.addArgument(new MethodCallExpr(new NameExpr(rulesMethodClass.getNameAsString()), methodName));
        }

        BodyDeclaration<?> rulesList = requiresMultipleRulesLists ?
                parseBodyDeclaration("java.util.List<org.drools.model.Rule> rules = new java.util.ArrayList<>(" + ruleCount + ");") :
                parseBodyDeclaration("java.util.List<org.drools.model.Rule> rules = getRulesList();");
        rulesClass.addMember(rulesList);
    }

    private void createAndAddGetRulesMethod( ClassOrInterfaceDeclaration rulesClass ) {
        BodyDeclaration<?> getRulesMethod = parseBodyDeclaration(
                "    @Override\n" +
                        "    public java.util.List<org.drools.model.Rule> getRules() {\n" +
                        "        return rules;\n" +
                        "    }\n"
        );
        rulesClass.addMember( getRulesMethod );

        StringBuilder sb = new StringBuilder("\n");
        sb.append("With the following expression ID:\n");
        sb.append(exprIdGenerator.toString());
        sb.append("\n");
        JavadocComment exprIdComment = new JavadocComment(sb.toString());
        getRulesMethod.setComment(exprIdComment);
    }

    private ClassOrInterfaceDeclaration createClass( String className, RuleSourceResult results ) {
        CompilationUnit cuRulesMethod = new CompilationUnit();
        results.withClass(cuRulesMethod);
        cuRulesMethod.setPackageDeclaration(name);
        manageImportForCompilationUnit(cuRulesMethod);
        cuRulesMethod.addImport(name + "." + rulesFileName, true, true);
        return cuRulesMethod.addClass(className);
    }

    private void buildArtifactsDeclaration( Collection<String> artifacts, ClassOrInterfaceDeclaration rulesClass,
                                            BlockStmt rulesListInitializerBody, String type, String fieldName, boolean needsToVar ) {
        if (!artifacts.isEmpty()) {
            BodyDeclaration<?> queriesList = parseBodyDeclaration("java.util.List<" + type + "> " + fieldName + " = new java.util.ArrayList<>();");
            rulesClass.addMember(queriesList);
            for (String name : artifacts) {
                addInitStatement( rulesListInitializerBody, new NameExpr( needsToVar ? toVar(name) : name ), fieldName );
            }
        } else {
            BodyDeclaration<?> queriesList = parseBodyDeclaration("java.util.List<" + type + "> " + fieldName + " = java.util.Collections.emptyList();");
            rulesClass.addMember(queriesList);
        }
    }

    private void addInitStatement( BlockStmt rulesListInitializerBody, Expression expr, String fieldName ) {
        NameExpr rulesFieldName = new NameExpr( fieldName );
        MethodCallExpr add = new MethodCallExpr( rulesFieldName, "add" );
        add.addArgument( expr );
        rulesListInitializerBody.addStatement( add );
    }

    private MethodCallExpr buildRulesField(RuleSourceResult results, int index) {
        CompilationUnit cu = new CompilationUnit();
        results.withClass(cu);
        cu.setPackageDeclaration(name);
        cu.addImport(new ImportDeclaration(new Name(Arrays.class.getCanonicalName()), false, false));
        cu.addImport(new ImportDeclaration(new Name(List.class.getCanonicalName()), false, false));
        cu.addImport(new ImportDeclaration(new Name(Rule.class.getCanonicalName()), false, false));
        String currentRulesMethodClassName = rulesFileName + "Rules" + index;
        ClassOrInterfaceDeclaration rulesClass = cu.addClass(currentRulesMethodClassName);
        rulesClass.addImplementedType(RulesSupplier.class);
        return buildRulesField( rulesClass );
    }

    private MethodCallExpr buildRulesField( ClassOrInterfaceDeclaration rulesClass ) {
        MethodCallExpr rulesInit = new MethodCallExpr( null, "java.util.Arrays.asList" );
        ClassOrInterfaceType rulesType = new ClassOrInterfaceType(null, new SimpleName("java.util.List"), new NodeList<Type>(toClassOrInterfaceType(Rule.class)));
        MethodDeclaration rulesGetter = new MethodDeclaration( NodeList.nodeList( publicModifier()), rulesType, "getRulesList" );
        rulesGetter.createBody().addStatement( new ReturnStmt(rulesInit ) );
        rulesClass.addMember( rulesGetter );
        return rulesInit;
    }

    private void manageImportForCompilationUnit(CompilationUnit cu) {
        // fixed part
        cu.addImport(new ImportDeclaration(new Name("org.drools.modelcompiler.dsl.pattern.D"), false, false));
        cu.addImport(new ImportDeclaration(new Name("org.drools.model.Index.ConstraintType"), false, false));

        // imports from DRL:
        for ( String i : imports ) {
            if ( i.equals(name+".*") ) {
                continue; // skip same-package star import.
            }
            cu.addImport( new ImportDeclaration(new Name(i), false, false ) );
        }
        for (String i : staticImports) {
            cu.addImport( new ImportDeclaration(new Name(i), true, false ) );
        }
    }

    private static void addGlobalField(ClassOrInterfaceDeclaration classDeclaration, String packageName, String globalName, Class<?> globalClass) {
        ClassOrInterfaceType varType = toClassOrInterfaceType(Global.class);
        varType.setTypeArguments(DrlxParseUtil.classToReferenceType(globalClass));
        Type declType = DrlxParseUtil.classToReferenceType(globalClass);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, GLOBAL_OF_CALL);
        declarationOfCall.addArgument(new ClassExpr(declType ));
        declarationOfCall.addArgument(new StringLiteralExpr(packageName));
        declarationOfCall.addArgument(new StringLiteralExpr(globalName));

        FieldDeclaration field = classDeclaration.addField(varType, toVar(globalName), publicModifier().getKeyword(), staticModifier().getKeyword(), finalModifier().getKeyword());

        field.getVariables().get(0).setInitializer(declarationOfCall);
    }

    public void setAccumulateFunctions(Map<String, AccumulateFunction> accumulateFunctions) {
        this.accumulateFunctions = accumulateFunctions;
    }

    public boolean hasDeclaration(String id) {
        return globals.get(id) != null;
    }

    public boolean registerDomainClass(Class<?> domainClass) {
        if (!domainClass.isPrimitive() && !domainClass.isArray()) {
            synchronized (domainClasses) {
                if (domainClasses.add(domainClass)) {
                    classDefinitionsMap.put(domainClass, createClassDefinition(domainClass));
                }
            }
            return true;
        }
        return false;
    }

    private ClassDefinition createClassDefinition(Class<?> domainClass) {
        ClassDefinition classDef = new ClassDefinition(domainClass);
        TypeDeclarationUtils.processModifiedProps(domainClass, classDef);
        return classDef;
    }

    public String getDomainClassesMetadataSource() {
        StringBuilder sb = new StringBuilder(
                "package " + name + ";\n" +
                "public class " + DOMAIN_CLASSESS_METADATA_FILE_NAME  + pkgUUID + " {\n\n"
        );
        for (Class<?> domainClass : domainClasses) {
            String domainClassSourceName = asJavaSourceName( domainClass );
            List<String> accessibleProperties = getAccessibleProperties( domainClass );
            sb.append( "    public static final " + DomainClassMetadata.class.getCanonicalName() + " " + domainClassSourceName + DOMAIN_CLASS_METADATA_INSTANCE + " = new " + domainClassSourceName+ "_Metadata();\n" );
            sb.append( "    private static class " + domainClassSourceName + "_Metadata implements " + DomainClassMetadata.class.getCanonicalName() + " {\n\n" );
            sb.append(
                    "        @Override\n" +
                    "        public Class<?> getDomainClass() {\n" +
                    "            return " + domainClass.getCanonicalName() + ".class;\n" +
                    "        }\n" +
                    "\n" +
                    "        @Override\n" +
                    "        public int getPropertiesSize() {\n" +
                    "            return " + accessibleProperties.size() + ";\n" +
                    "        }\n\n" +
                    "        @Override\n" +
                    "        public int getPropertyIndex( String name ) {\n" +
                    "            switch(name) {\n"
            );
            for (int i = 0; i < accessibleProperties.size(); i++) {
                sb.append( "                case \"" + accessibleProperties.get(i) + "\": return " + i + ";\n" );
            }
            sb.append(
                    "             }\n" +
                    "             throw new RuntimeException(\"Unknown property '\" + name + \"' for class class " + domainClass + "\");\n" +
                    "        }\n" +
                    "    }\n\n"
            );
        }
        sb.append( "}" );

        return sb.toString();
    }

    public Map<LambdaExpr, java.lang.reflect.Type> getLambdaReturnTypes() {
        return lambdaReturnTypes;
    }

    public void registerLambdaReturnType(LambdaExpr lambdaExpr, java.lang.reflect.Type type) {
        lambdaReturnTypes.put(lambdaExpr, type);
    }

    public void indexConstraint(String exprId, PredicateInformation predicateInformation) {
        allConstraintsMap.put(exprId, predicateInformation);
    }

    public Optional<PredicateInformation> findConstraintWithExprId(String exprId) {
        return ofNullable(allConstraintsMap.get(exprId));
    }

    public Map<String, PredicateInformation> getAllConstraintsMap() {
        return Collections.unmodifiableMap(allConstraintsMap);
    }

    public ClassDefinition getClassDefinition(Class<?> cls) {
        return classDefinitionsMap.get(cls);
    }

}

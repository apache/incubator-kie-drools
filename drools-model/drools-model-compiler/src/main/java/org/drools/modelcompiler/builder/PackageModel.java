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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.Modifier;
import org.drools.javaparser.ast.body.BodyDeclaration;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.FieldDeclaration;
import org.drools.javaparser.ast.body.InitializerDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;
import org.drools.javaparser.ast.body.VariableDeclarator;
import org.drools.javaparser.ast.comments.JavadocComment;
import org.drools.javaparser.ast.expr.AssignExpr;
import org.drools.javaparser.ast.expr.ClassExpr;
import org.drools.javaparser.ast.expr.MethodCallExpr;
import org.drools.javaparser.ast.expr.NameExpr;
import org.drools.javaparser.ast.expr.StringLiteralExpr;
import org.drools.javaparser.ast.expr.VariableDeclarationExpr;
import org.drools.javaparser.ast.stmt.BlockStmt;
import org.drools.javaparser.ast.type.ClassOrInterfaceType;
import org.drools.javaparser.ast.type.Type;
import org.drools.javaparser.printer.PrettyPrinter;
import org.drools.javaparser.printer.PrettyPrinterConfiguration;
import org.drools.model.Global;
import org.drools.model.Model;
import org.drools.modelcompiler.builder.generator.DRLExprIdGenerator;
import org.drools.modelcompiler.builder.generator.ModelGenerator;

public class PackageModel {

    private final String name;
    
    private Set<String> imports = new HashSet<>();

    private Map<String, Class<?>> globals = new HashMap<>();

    private Map<String, MethodDeclaration> ruleMethods = new HashMap<>();

    private Map<String, MethodDeclaration> queryMethods = new HashMap<>();

    private Map<String, List<ModelGenerator.QueryParameter>> queryVariables = new HashMap<>();

    private DRLExprIdGenerator exprIdGenerator;

    public PackageModel( String name ) {
        this.name = name;
        exprIdGenerator = new DRLExprIdGenerator();
    }

    public String getName() {
        return name;
    }
    
    public DRLExprIdGenerator getExprIdGenerator() {
        return exprIdGenerator;
    }

    public void addImports(Collection<String> imports) {
        this.imports.addAll(imports);
    }

    public void addGlobals(Map<String, String> values) {
        Map<String, Class<?>> transformed;
        transformed = values
                .entrySet()
                .stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> {
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

    public void putRuleMethod(String methodName, MethodDeclaration ruleMethod) {
        this.ruleMethods.put(methodName, ruleMethod);
    }

    public void putQueryMethod(String methodName, MethodDeclaration queryMethod) {
        this.queryMethods.put(methodName, queryMethod);
    }

    public MethodDeclaration getQueryMethod(String key) {
        return queryMethods.get(key);
    }

    public void putQueryVariable(String queryName, ModelGenerator.QueryParameter qp) {
        this.queryVariables.computeIfAbsent(queryName, k -> new ArrayList<>());
        this.queryVariables.get(queryName).add(qp);
    }

    public List<ModelGenerator.QueryParameter> queryVariables(String queryName) {
        return this.queryVariables.get(queryName);
    }

    public String getVarsSource() {
//        if (true) return getVariableSource();
        return null;
    }

    public String getRulesSource() {
//        if (true) return getRuleModelSource();

        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( name );

        // fixed part
        cu.addImport(JavaParser.parseImport("import java.util.*;"                          ));
        cu.addImport(JavaParser.parseImport("import org.drools.model.*;"                   ));
        cu.addImport(JavaParser.parseImport("import static org.drools.model.DSL.*;"        ));
        cu.addImport(JavaParser.parseImport("import org.drools.model.Index.ConstraintType;"));
        
        // imports from DRL:
        for ( String i : imports ) {
            if ( i.equals(name+".*") ) {
                continue; // skip same-package star import.
            }
            cu.addImport(JavaParser.parseImport("import "+i+";"));
        }
        
        ClassOrInterfaceDeclaration rulesClass = cu.addClass("Rules");
        rulesClass.addImplementedType(Model.class);

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

        BodyDeclaration<?> rulesList = JavaParser.parseBodyDeclaration("List<Rule> rules = new ArrayList<>();");
        rulesClass.addMember(rulesList);
        BodyDeclaration<?> queriesList = JavaParser.parseBodyDeclaration("List<Query> queries = new ArrayList<>();");
        rulesClass.addMember(queriesList);
        BodyDeclaration<?> globalsList = JavaParser.parseBodyDeclaration("List<Global> globals = new ArrayList<>();");
        rulesClass.addMember(globalsList);
        // end of fixed part

        for ( Map.Entry<String, Class<?>> g : getGlobals().entrySet() ) {
            addGlobal(rulesClass, getName(), g.getKey(), g.getValue());
        }
        
        // instance initializer block.
        // add to `rules` list the result of invoking each method for rule 
        InitializerDeclaration rulesListInitializer = new InitializerDeclaration();
        rulesClass.addMember(rulesListInitializer);
        BlockStmt rulesListInitializerBody = new BlockStmt();
        rulesListInitializer.setBody(rulesListInitializerBody);
        for ( String methodName : ruleMethods.keySet() ) {
            NameExpr rulesFieldName = new NameExpr( "rules" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new MethodCallExpr(null, methodName) );
            rulesListInitializerBody.addStatement( add );
        }

        for ( String methodName : queryMethods.keySet() ) {
            NameExpr rulesFieldName = new NameExpr( "queries" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new MethodCallExpr(null, methodName) );
            rulesListInitializerBody.addStatement( add );
        }

        for ( Map.Entry<String, Class<?>> g : getGlobals().entrySet() ) {
            NameExpr rulesFieldName = new NameExpr( "globals" );
            MethodCallExpr add = new MethodCallExpr(rulesFieldName, "add");
            add.addArgument( new NameExpr("glb_" + g.getKey()) );
            rulesListInitializerBody.addStatement( add );

        }

        // each method per Drlx parser result
        ruleMethods.values().forEach( rulesClass::addMember );
        queryMethods.values().forEach(rulesClass::addMember);
        
        PrettyPrinterConfiguration config = new PrettyPrinterConfiguration();
        config.setColumnAlignParameters(true);
//        config.setColumnAlignFirstMethodChain(true);
        return new PrettyPrinter(config).print(cu);
    }

    private static void addGlobal(ClassOrInterfaceDeclaration classDeclaration, String packageName, String globalName, Class<?> globalClass) {
        ClassOrInterfaceType varType = JavaParser.parseClassOrInterfaceType(Global.class.getCanonicalName());
        varType.setTypeArguments(ModelGenerator.classToReferenceType(globalClass));
        Type declType = ModelGenerator.classToReferenceType(globalClass);

        MethodCallExpr declarationOfCall = new MethodCallExpr(null, "globalOf");
        MethodCallExpr typeCall = new MethodCallExpr(null, "type");
        typeCall.addArgument( new ClassExpr(declType ));
        declarationOfCall.addArgument(typeCall);
        declarationOfCall.addArgument(new StringLiteralExpr(packageName));

        FieldDeclaration field = classDeclaration.addField(varType, "glb_" + globalName, Modifier.FINAL);

        field.getVariables().get(0).setInitializer(declarationOfCall);
    }


    public void print() {
        System.out.println("=====");
        System.out.println("PackageModel "+name);
        System.out.println("    imports: "+imports);
        System.out.println(getRulesSource());
        System.out.println("=====");
    }
    
    @SuppressWarnings("unused")
    @Deprecated
    private static String getVariableSource() {
        return "package myrules;\n" +
               "" +
               "import org.drools.model.*;\n" +
               "import static org.drools.model.DSL.*;\n" +
               "import org.drools.modelcompiler.Person;\n" +
               "" +
               "public class Variables {\n" +
               "" +
               "    public static final Variable<Person> markV = declarationOf( type( Person.class ) );\n" +
               "    public static final Variable<Person> olderV = declarationOf( type( Person.class ) );\n" +
               "}\n";
    }

    @SuppressWarnings("unused")
    @Deprecated
    private static String getRuleModelSource() {
        return "package myrules;\n" +
               "" +
               "import java.util.*;\n" +
               "import org.drools.model.*;\n" +
               "import static org.drools.model.DSL.*;\n" +
               "import org.drools.modelcompiler.Person;\n" +
               "import org.drools.model.Index.ConstraintType;\n" +
               "" +
               "import static myrules.Variables.*;\n" +
               "" +
               "public class Rules implements Model {\n" +
               "" +
               "    int a;\n" + // workaround for ecj bug!
               "" +
               "    @Override\n" +
               "    public List<Rule> getRules() {\n" +
               "        return Arrays.asList( rule1() );\n" +
               "    }\n" +
               "" +
               "    private Rule rule1() {\n" +
               "        Rule rule = rule( \"beta\" )\n" +
               "                .view(\n" +
               "                        expr(markV, p -> p.getName().equals(\"Mark\"))\n" +
               "                                .indexedBy( String.class, ConstraintType.EQUAL, Person::getName, \"Mark\" )\n" +
               "                                .reactOn( \"name\", \"age\" ),\n" +
               "                        expr(olderV, p -> !p.getName().equals(\"Mark\"))\n" +
               "                                .indexedBy( String.class, ConstraintType.NOT_EQUAL, Person::getName, \"Mark\" )\n" +
               "                                .reactOn( \"name\" ),\n" +
               "                        expr(olderV, markV, (p1, p2) -> p1.getAge() > p2.getAge())\n" +
               "                                .indexedBy( int.class, ConstraintType.GREATER_THAN, Person::getAge, Person::getAge )\n" +
               "                                .reactOn( \"age\" )\n" +
               "                     )\n" +
               "                .then(c -> c.on(olderV, markV)\n" +
               "                            .execute( (p1, p2) -> System.out.println( p1.getName() + \" is older than \" + p2.getName() ) ) );\n" +
               "        return rule;\n" +
               "    }\n" +
               "}\n";
    }



}

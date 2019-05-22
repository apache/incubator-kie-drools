package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.UnknownType;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.Drools;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.printer.PrettyPrinter;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.Drools;
import org.drools.modelcompiler.builder.PackageModel.RuleSourceResult;
import org.kie.api.builder.ReleaseId;

import static com.github.javaparser.ast.NodeList.nodeList;
import static org.assertj.core.api.Assertions.*;
import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;
import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class ModelWriter {

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageModel> packageModels) {
        List<String> sourceFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();

        PrettyPrinter prettyPrinter = getPrettyPrinter();

        for (PackageModel pkgModel : packageModels) {
            String pkgName = pkgModel.getName();
            String folderName = pkgName.replace( '.', '/' );

            for (ClassOrInterfaceDeclaration generatedPojo : pkgModel.getGeneratedPOJOsSource()) {
                final String source = JavaParserCompiler.toPojoSource( pkgModel.getName(), pkgModel.getImports(), pkgModel.getStaticImports(), generatedPojo );
                pkgModel.logRule( source );
                String pojoSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getName() + ".java";
                srcMfs.write( pojoSourceName, source.getBytes() );
                sourceFiles.add( pojoSourceName );
            }

            for (GeneratedClassWithPackage generatedPojo : pkgModel.getGeneratedAccumulateClasses()) {
                final String source = JavaParserCompiler.toPojoSource( pkgModel.getName(), generatedPojo.getImports(), pkgModel.getStaticImports(), generatedPojo.getGeneratedClass() );
                pkgModel.logRule( source );
                String pojoSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getGeneratedClass().getName() + ".java";
                srcMfs.write( pojoSourceName, source.getBytes() );
                sourceFiles.add( pojoSourceName );
            }

            RuleSourceResult rulesSourceResult = pkgModel.getRulesSource();
            // main rules file:
            String rulesFileName = pkgModel.getRulesFileName();
            String rulesSourceName = "src/main/java/" + folderName + "/" + rulesFileName + ".java";
            String rulesSource = prettyPrinter.print( rulesSourceResult.getMainRuleClass() );
            pkgModel.logRule( rulesSource );
            byte[] rulesBytes = rulesSource.getBytes();
            srcMfs.write( rulesSourceName, rulesBytes );
            modelFiles.add( pkgName + "." + rulesFileName );
            sourceFiles.add( rulesSourceName );
            // manage additional classes, please notice to not add to modelFiles.
            for (CompilationUnit cu : rulesSourceResult.getSplitted()) {
                String addFileName = cu.findFirst( ClassOrInterfaceDeclaration.class ).get().getNameAsString();
                String addSourceName = "src/main/java/" + folderName + "/" + addFileName + ".java";
                debugPrettyPrinter(prettyPrinter, cu);
                prettyPrinter.print(cu);
                String addSource = prettyPrinter.print( cu );
                pkgModel.logRule( addSource );
                byte[] addBytes = addSource.getBytes();
                srcMfs.write( addSourceName, addBytes );
                sourceFiles.add( addSourceName );
            }
        }

        return new Result(sourceFiles, modelFiles);
    }

    public void writeModelFile(Collection<String> modelSources, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if(!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(getModelFileWithGAV(releaseId), pkgNames.getBytes() );
    }

    public static class Result {
        private final List<String> sourceFiles;
        private final List<String> modelFiles;

        public Result( List<String> sourceFiles, List<String> modelFiles ) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
        }

        public String[] getSources() {
            return sourceFiles.toArray( new String[sourceFiles.size()] );
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }

    }
    private void debugPrettyPrinter(PrettyPrinter prettyPrinter, CompilationUnit bugCu) {
        CompilationUnit cu = recreateAST();

        assertThat(cu).isEqualToComparingFieldByFieldRecursively(bugCu);

        prettyPrinter.print(cu);
        prettyPrinter.print(bugCu);


    }

    private CompilationUnit recreateAST() {
        CompilationUnit cu = new CompilationUnit();

        cu.setPackageDeclaration("defaultpkg");
        cu.addImport("defaultpkg.Rules", true, true);

        ClassOrInterfaceDeclaration clazz = cu.addClass("Rules", Modifier.Keyword.PUBLIC);

        NodeList<Modifier> publicStatic = nodeList(Modifier.publicModifier(), Modifier.staticModifier());
        ClassOrInterfaceType ruleType = new ClassOrInterfaceType(null, "org.drools.model.Rule");
        MethodDeclaration look = new MethodDeclaration(publicStatic, ruleType, "rule_look");
        BlockStmt lookstmt = new BlockStmt();

        String variableName = "rule";
        VariableDeclarationExpr variableDeclarationExpr = new VariableDeclarationExpr(ruleType, variableName);
        ReturnStmt returnStmt1 = new ReturnStmt(new NameExpr(variableName));

        MethodCallExpr ruleMethod = new MethodCallExpr(new NameExpr("D"), "rule", nodeList(new StringLiteralExpr("look")));

        ClassOrInterfaceType droolsImpls = new ClassOrInterfaceType(null, "org.drools.modelcompiler.consequence.DroolsImpl");

        Expression castExpr = new EnclosedExpr(new CastExpr(droolsImpls, new NameExpr("drools")));
        MethodCallExpr asKnowledgeHelper = new MethodCallExpr(castExpr, "asKnowledgeHelper");

        Expression insertLogical = new MethodCallExpr(asKnowledgeHelper, "insertLogical", nodeList(new IntegerLiteralExpr(1)));

        NodeList<Statement> xes = nodeList(new ExpressionStmt(insertLogical));
        Type unknownType = new UnknownType();
        LambdaExpr droolsLambda = new LambdaExpr(new Parameter(unknownType, new SimpleName("drools")), new BlockStmt(xes));
        droolsLambda.setEnclosingParameters(true);

        MethodCallExpr executeMethod = new MethodCallExpr(new NameExpr("D"), "execute", nodeList(droolsLambda));
        MethodCallExpr build = new MethodCallExpr(ruleMethod, "build", nodeList(executeMethod));

        AssignExpr assignExpr = new AssignExpr(variableDeclarationExpr, build, AssignExpr.Operator.ASSIGN);
        ExpressionStmt expressionStmt = new ExpressionStmt(assignExpr);

        lookstmt.addStatement(expressionStmt);
        lookstmt.addStatement(returnStmt1);

        look.setBody(lookstmt);
        clazz.addMember(look);

        MethodDeclaration go1 = new MethodDeclaration(publicStatic, ruleType, "rule_go1");

        BlockStmt gostmt = new BlockStmt();

        VariableDeclarationExpr variableDeclarationExpr2 = new VariableDeclarationExpr(ruleType, variableName);
        ReturnStmt returnStmt2 = new ReturnStmt(new NameExpr(variableName));

        MethodCallExpr ruleMethod2 = new MethodCallExpr(new NameExpr("D"), "rule", nodeList(new StringLiteralExpr("go1")));

        Expression castExpr2 = new EnclosedExpr(new CastExpr(droolsImpls, new NameExpr("drools")));
        MethodCallExpr asKnowledgeHelper2 = new MethodCallExpr(castExpr2, "asKnowledgeHelper");

        Expression getRuleExpr = new MethodCallExpr(asKnowledgeHelper2, "getRule");
        Expression getNameExpr = new MethodCallExpr(getRuleExpr, "getName");

        Expression listAdd = new MethodCallExpr(new NameExpr("list"), "add", nodeList(getNameExpr));

        NodeList<Statement> xes2 = nodeList(new ExpressionStmt(listAdd));
        LambdaExpr droolsLambda2 = new LambdaExpr(new Parameter(unknownType, new SimpleName("drools")), new BlockStmt(xes2));
        droolsLambda2.setEnclosingParameters(true);
        MethodCallExpr executeMethod2 = new MethodCallExpr(new NameExpr("D"), "execute", nodeList(droolsLambda2));
        MethodCallExpr build2 = new MethodCallExpr(ruleMethod2, "build", nodeList(executeMethod2));
        AssignExpr assignExpr2 = new AssignExpr(variableDeclarationExpr2, build2, AssignExpr.Operator.ASSIGN);
        ExpressionStmt expressionStmt2 = new ExpressionStmt(assignExpr2);

        gostmt.addStatement(expressionStmt2);
        gostmt.addStatement(returnStmt2);
        go1.setBody(gostmt);

        clazz.addMember(go1);
        return cu;
    }
}

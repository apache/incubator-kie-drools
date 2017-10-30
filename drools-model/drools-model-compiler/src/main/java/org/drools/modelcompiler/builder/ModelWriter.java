package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.printer.PrettyPrinter;

import static org.drools.modelcompiler.CanonicalKieModule.PACKAGE_LIST;
import static org.drools.modelcompiler.CanonicalKieModule.RULES_FILE_NAME;
import static org.drools.modelcompiler.CanonicalKieModule.VARIABLES_FILE_NAME;
import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class ModelWriter {

    public Result writeModel(MemoryFileSystem srcMfs, List<PackageModel> packageModels) {
        List<String> sources = new ArrayList<>();
        List<String> pkgNames = new ArrayList<>();

        PrettyPrinter prettyPrinter = getPrettyPrinter();

        for (PackageModel pkgModel : packageModels) {
            String pkgName = pkgModel.getName();
            pkgNames.add(pkgName);
            String folderName = pkgName.replace( '.', '/' );

            if ( pkgModel.getVarsSource() != null ) {
                String varsSourceName = "src/main/java/" + folderName + "/" + VARIABLES_FILE_NAME + ".java";
                byte[] varsBytes = pkgModel.getVarsSource().getBytes();
                srcMfs.write( varsSourceName, varsBytes );
                sources.add( varsSourceName );
            }

            for (ClassOrInterfaceDeclaration generatedPojo : pkgModel.getGeneratedPOJOsSource()) {
                final String source = toPojoSource( pkgModel.getName(), prettyPrinter, generatedPojo );
                pkgModel.print( source );
                final String varsSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getName() + ".java";
                srcMfs.write( varsSourceName, source.getBytes() );
                sources.add( varsSourceName );
            }

            String rulesSourceName = "src/main/java/" + folderName + "/" + RULES_FILE_NAME + ".java";
            String rulesSource = pkgModel.getRulesSource( prettyPrinter );
            pkgModel.print( rulesSource );
            byte[] rulesBytes = rulesSource.getBytes();
            srcMfs.write( rulesSourceName, rulesBytes );
            sources.add( rulesSourceName );
        }

        return new Result(sources.toArray(new String[sources.size()]), pkgNames);
    }

    private String toPojoSource(String packageName, PrettyPrinter prettyPrinter, ClassOrInterfaceDeclaration pojo ) {
        CompilationUnit cu = new CompilationUnit();
        cu.setPackageDeclaration( packageName );

        // fixed part
        cu.addImport(JavaParser.parseImport("import java.util.*;" ) );
        cu.addImport( JavaParser.parseImport( "import org.drools.model.*;" ) );
        cu.addImport( JavaParser.parseImport( "import static org.drools.model.DSL.*;" ) );
        cu.addImport( JavaParser.parseImport( "import org.drools.model.Index.ConstraintType;" ) );

        cu.addType( pojo );

        return prettyPrinter.print( cu );
    }

    public void writePackages(List<String> packages, MemoryFileSystem trgMfs) {
        final String pkgNames = packages.stream().collect(Collectors.joining("\n"));
        trgMfs.write(PACKAGE_LIST, pkgNames.getBytes() );
    }

    public static class Result {
        final private String[] sources;
        final private List<String> packages;

        public Result(String[] sources, List<String> packages) {
            this.sources = sources;
            this.packages = packages;
        }

        public String[] getSources() {
            return sources;
        }

        public List<String> getPackages() {
            return packages;
        }
    }

}

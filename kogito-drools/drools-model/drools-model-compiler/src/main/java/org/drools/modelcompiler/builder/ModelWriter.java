package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;

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

    public String[] writeModel(MemoryFileSystem srcMfs, MemoryFileSystem trgMfs, List<PackageModel> packageModels) {
        List<String> sources = new ArrayList<>();
        StringBuilder pkgNames = new StringBuilder();

        PrettyPrinter prettyPrinter = getPrettyPrinter();

        for (PackageModel pkgModel : packageModels) {
            String pkgName = pkgModel.getName();
            pkgNames.append( pkgName ).append( "\n" );
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

        trgMfs.write( PACKAGE_LIST, pkgNames.toString().getBytes() );
        return sources.toArray( new String[sources.size()] );
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

}

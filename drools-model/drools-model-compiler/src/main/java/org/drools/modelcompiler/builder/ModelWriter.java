package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.printer.PrettyPrinter;

import static org.drools.core.util.StringUtils.generateUUID;
import static org.drools.modelcompiler.CanonicalKieModule.MODEL_FILE;
import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;

public class ModelWriter {

    public static final String RULES_FILE_NAME = "Rules";

    public Result writeModel(MemoryFileSystem srcMfs, List<PackageModel> packageModels) {
        List<String> sourceFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();

        PrettyPrinter prettyPrinter = getPrettyPrinter();

        for (PackageModel pkgModel : packageModels) {
            String pkgName = pkgModel.getName();
            String folderName = pkgName.replace( '.', '/' );

            for (ClassOrInterfaceDeclaration generatedPojo : pkgModel.getGeneratedPOJOsSource()) {
                final String source = toPojoSource( pkgModel.getName(), prettyPrinter, generatedPojo );
                pkgModel.print( source );
                String pojoSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getName() + ".java";
                srcMfs.write( pojoSourceName, source.getBytes() );
                sourceFiles.add( pojoSourceName );
            }

            String rulesFileName = generateRulesFileName();
            String rulesSourceName = "src/main/java/" + folderName + "/" + rulesFileName + ".java";
            String rulesSource = pkgModel.getRulesSource( prettyPrinter, rulesFileName, pkgName );
            pkgModel.print( rulesSource );
            byte[] rulesBytes = rulesSource.getBytes();
            srcMfs.write( rulesSourceName, rulesBytes );
            modelFiles.add( pkgName + "." + rulesFileName );
            sourceFiles.add( rulesSourceName );
        }

        return new Result(sourceFiles, modelFiles);
    }

    private String generateRulesFileName() {
        return RULES_FILE_NAME + generateUUID();
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

    public void writeModelFile( List<String> modelSources, MemoryFileSystem trgMfs) {
        final String pkgNames = modelSources.stream().collect(Collectors.joining("\n"));
        trgMfs.write( MODEL_FILE, pkgNames.getBytes() );
    }

    public static class Result {
        final List<String> sourceFiles;
        final List<String> modelFiles;

        public Result( List<String> sourceFiles, List<String> modelFiles ) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
        }

        public String[] getSources() {
            return sourceFiles.toArray( new String[sourceFiles.size()] );
        }
    }
}

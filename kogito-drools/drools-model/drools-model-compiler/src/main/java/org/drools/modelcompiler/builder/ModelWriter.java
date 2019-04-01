package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.printer.PrettyPrinter;
import org.drools.modelcompiler.builder.PackageModel.RuleSourceResult;

import static org.drools.modelcompiler.builder.JavaParserCompiler.getPrettyPrinter;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;

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
                String pojoSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getName() + ".java";
                addSource( srcMfs, sourceFiles, pkgModel, pojoSourceName, source );
            }

            for (GeneratedClassWithPackage generatedPojo : pkgModel.getGeneratedAccumulateClasses()) {
                final String source = JavaParserCompiler.toPojoSource( pkgModel.getName(), generatedPojo.getImports(), pkgModel.getStaticImports(), generatedPojo.getGeneratedClass() );
                String pojoSourceName = "src/main/java/" + folderName + "/" + generatedPojo.getGeneratedClass().getName() + ".java";
                addSource( srcMfs, sourceFiles, pkgModel, pojoSourceName, source );
            }

            RuleSourceResult rulesSourceResult = pkgModel.getRulesSource();
            // main rules file:
            String rulesFileName = pkgModel.getRulesFileName();
            String rulesSourceName = "src/main/java/" + folderName + "/" + rulesFileName + ".java";
            String rulesSource = prettyPrinter.print( rulesSourceResult.getMainRuleClass() );
            addSource( srcMfs, sourceFiles, pkgModel, rulesSourceName, rulesSource );
            modelFiles.add( pkgName + "." + rulesFileName );
            // manage additional classes, please notice to not add to modelFiles.
            for (CompilationUnit cu : rulesSourceResult.getSplitted()) {
                String addFileName = cu.findFirst( ClassOrInterfaceDeclaration.class ).get().getNameAsString();
                String sourceName = "src/main/java/" + folderName + "/" + addFileName + ".java";
                addSource( srcMfs, sourceFiles, pkgModel, sourceName, prettyPrinter.print( cu ) );
            }

            String sourceName = "src/main/java/" + folderName + "/" + DOMAIN_CLASSESS_METADATA_FILE_NAME + pkgModel.getPackageUUID() + ".java";
            addSource( srcMfs, sourceFiles, pkgModel, sourceName, pkgModel.getDomainClassesMetadataSource() );
            pkgModel.getModuleGenerator().write(srcMfs);
        }

        return new Result(sourceFiles, modelFiles);
    }

    private void addSource( MemoryFileSystem srcMfs, List<String> sourceFiles, PackageModel pkgModel, String sourceName, String source ) {
        pkgModel.logRule( source );
        srcMfs.write( sourceName, source.getBytes() );
        sourceFiles.add( sourceName );
    }

    public static class Result {
        private final List<String> sourceFiles;
        private final List<String> modelFiles;

        public Result( List<String> sourceFiles, List<String> modelFiles ) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
        }

        public List<String> getSources() {
            return sourceFiles;
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }
    }
}

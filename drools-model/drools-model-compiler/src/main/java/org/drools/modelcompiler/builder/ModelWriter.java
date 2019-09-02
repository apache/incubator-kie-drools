package org.drools.modelcompiler.builder;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.Drools;
import org.kie.api.builder.ReleaseId;

import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;
import static org.drools.modelcompiler.builder.PackageModel.DOMAIN_CLASSESS_METADATA_FILE_NAME;

public class ModelWriter {

    private final String basePath;

    public ModelWriter() {
        this("src/main/java");
    }

    public ModelWriter(String basePath) {
        this.basePath = basePath;
    }

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageModel> packageModels) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();

        for (PackageModel pkgModel : packageModels) {
            String pkgName = pkgModel.getName();
            String folderName = pkgName.replace( '.', '/' );

            PackageModelWriter packageModelWriter = new PackageModelWriter(pkgModel);
            for (DeclaredTypeWriter declaredType : packageModelWriter.getDeclaredTypes()) {
                generatedFiles.add(new GeneratedFile(declaredType.getName(), declaredType.getSource()));
            }

            for (AccumulateClassWriter accumulateClassWriter : packageModelWriter.getAccumulateClasses()) {
                generatedFiles.add(new GeneratedFile(accumulateClassWriter.getName(), accumulateClassWriter.getSource()));
            }

            RuleWriter rules = packageModelWriter.getRules();
            generatedFiles.add(new GeneratedFile(rules.getName(), rules.getMainSource()));
            modelFiles.add( rules.getClassName() );

            for (RuleWriter.RuleFileSource ruleSource : rules.getRuleSources()) {
                generatedFiles.add(new GeneratedFile(ruleSource.getName(), ruleSource.getSource()));
            }

            String sourceName = "src/main/java/" + folderName + "/" + DOMAIN_CLASSESS_METADATA_FILE_NAME + pkgModel.getPackageUUID() + ".java";
            generatedFiles.add( new GeneratedFile( sourceName, pkgModel.getDomainClassesMetadataSource() ) );
        }

        List<String> sourceFiles = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedFiles) {
            String path = basePath + "/" + generatedFile.getPath();
            sourceFiles.add(path);
            srcMfs.write(path, generatedFile.getData());
        }

        return new Result(sourceFiles, modelFiles);
    }

    private String pojoName(String folderName, String nameAsString) {
        return basePath + "/" + folderName + "/" + nameAsString + ".java";
    }

    public void writeModelFile(Collection<String> modelSources, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if (!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(getModelFileWithGAV(releaseId), pkgNames.getBytes());
    }

    private static class GeneratedFile {

        final String path;
        final byte[] data;

        private GeneratedFile(String path, String data) {
            this.path = path;
            this.data = data.getBytes(StandardCharsets.UTF_8);
        }

        private GeneratedFile(String path, byte[] data) {
            this.path = path;
            this.data = data;
        }

        public byte[] getData() {
            return data;
        }

        public String getPath() {
            return path;
        }

        @Override
        public String toString() {
            return "GeneratedFile{" +
                    "path='" + path + '\'' +
                    '}';
        }
    }

    public static class Result {

        private final List<String> sourceFiles;
        private final List<String> modelFiles;

        public Result(List<String> sourceFiles, List<String> modelFiles) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
        }

        public String[] getSources() {
            return sourceFiles.toArray(new String[sourceFiles.size()]);
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }
    }
}

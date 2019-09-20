package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.core.util.Drools;
import org.kie.api.builder.ReleaseId;

import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;

public class ModelWriter {

    private final String basePath;

    public ModelWriter() {
        this("src/main/java");
    }

    public ModelWriter(String basePath) {
        this.basePath = basePath;
    }

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageSources> packageSources) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();

        for (PackageSources pkgSources : packageSources) {
            generatedFiles.addAll( pkgSources.getPojoSources() );
            generatedFiles.addAll( pkgSources.getAccumulateSources() );
            generatedFiles.add( pkgSources.getMainSource() );
            generatedFiles.addAll( pkgSources.getRuleSources() );
            generatedFiles.add( pkgSources.getDomainClassSource() );
            modelFiles.add( pkgSources.getModelName() );
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

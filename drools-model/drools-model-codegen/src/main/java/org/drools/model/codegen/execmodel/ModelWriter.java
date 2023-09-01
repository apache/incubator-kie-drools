package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.base.util.Drools;
import org.kie.api.builder.ReleaseId;
import org.drools.util.PortablePath;

import static org.drools.modelcompiler.CanonicalKieModule.MODEL_VERSION;
import static org.drools.modelcompiler.CanonicalKieModule.RULE_UNIT_SERVICES_FILE;
import static org.drools.modelcompiler.CanonicalKieModule.getGeneratedClassNamesFile;
import static org.drools.modelcompiler.CanonicalKieModule.getModelFileWithGAV;

public class ModelWriter {

    private final PortablePath basePath;

    public ModelWriter() {
        this("src/main/java");
    }

    public ModelWriter(String basePath) {
        this.basePath = PortablePath.of(basePath);
    }

    public Result writeModel(MemoryFileSystem srcMfs, Collection<PackageSources> packageSources) {
        List<GeneratedFile> generatedFiles = new ArrayList<>();
        List<String> modelFiles = new ArrayList<>();
        List<String> ruleUnitClassNames = new ArrayList<>();

        for (PackageSources pkgSources : packageSources) {
            pkgSources.collectGeneratedFiles( generatedFiles );
            modelFiles.addAll( pkgSources.getModelNames() );
            ruleUnitClassNames.addAll( pkgSources.getRuleUnitClassNames() );
        }

        List<String> sourceFiles = new ArrayList<>();
        for (GeneratedFile generatedFile : generatedFiles) {
            PortablePath path = basePath.resolve(generatedFile.getKiePath());
            sourceFiles.add(path.asString());
            srcMfs.write(path, generatedFile.getData());
        }

        return new Result(sourceFiles, modelFiles, ruleUnitClassNames);
    }

    public PortablePath getBasePath() {
        return basePath;
    }

    public void writeModelFile( Collection<String> modelSources, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        String pkgNames = MODEL_VERSION + Drools.getFullVersion() + "\n";
        if (!modelSources.isEmpty()) {
            pkgNames += modelSources.stream().collect(Collectors.joining("\n"));
        }
        trgMfs.write(getModelFileWithGAV(releaseId), pkgNames.getBytes());
    }

    public void writeRuleUnitServiceFile(Collection<String> ruleUnitClassNames, MemoryFileSystem trgMfs) {
        if (!ruleUnitClassNames.isEmpty()) {
            trgMfs.write(RULE_UNIT_SERVICES_FILE, ruleUnitClassNames.stream().collect(Collectors.joining("\n")).getBytes());
        }
    }

    public void writeGeneratedClassNamesFile(Set<String> generatedClassNames, MemoryFileSystem trgMfs, ReleaseId releaseId) {
        trgMfs.write(getGeneratedClassNamesFile(releaseId), generatedClassNamesFileContent(generatedClassNames).getBytes());
    }

    public static String generatedClassNamesFileContent(Set<String> generatedClassNames) {
        String content = "";
        if (!generatedClassNames.isEmpty()) {
            content = generatedClassNames.stream().collect(Collectors.joining("\n"));
        }
        return content;
    }

    public static class Result {

        private final List<String> sourceFiles;
        private final List<String> modelFiles;
        private final List<String> ruleUnitClassNames;

        public Result(List<String> sourceFiles, List<String> modelFiles, List<String> ruleUnitClassNames) {
            this.sourceFiles = sourceFiles;
            this.modelFiles = modelFiles;
            this.ruleUnitClassNames = ruleUnitClassNames;
        }

        public List<String> getSourceFiles() {
            return sourceFiles;
        }

        public List<String> getModelFiles() {
            return modelFiles;
        }

        public List<String> getRuleUnitClassNames() {
            return ruleUnitClassNames;
        }
    }
}

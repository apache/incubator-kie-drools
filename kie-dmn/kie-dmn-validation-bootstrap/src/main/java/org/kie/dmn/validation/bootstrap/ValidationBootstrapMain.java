package org.kie.dmn.validation.bootstrap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.compiler.compiler.io.Folder;
import org.drools.compiler.compiler.io.memory.MemoryFile;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kie.builder.impl.MemoryKieModule;
import org.drools.compiler.kie.builder.impl.ResultsImpl;
import org.drools.core.util.IoUtils;
import org.drools.modelcompiler.CanonicalKieModule;
import org.drools.modelcompiler.builder.CanonicalModelKieProject;
import org.drools.modelcompiler.builder.ModelBuilderImpl;
import org.drools.modelcompiler.builder.ModelWriter;
import org.kie.api.KieServices;
import org.kie.api.builder.Results;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationBootstrapMain {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationBootstrapMain.class);

    public static void main(String[] args) throws IOException {
        System.out.println("Invoked with:" + Arrays.asList(args));
        File kieDmnValidationBaseDir = new File(args[0]);
        KieServices ks = KieServices.Factory.get();
        final KieBuilderImpl kieBuilder = (KieBuilderImpl) ks.newKieBuilder(kieDmnValidationBaseDir);

        kieBuilder.buildAll(ASD::new,
                            s -> !s.contains("src/test/java") && !s.contains("src\\test\\java") &&
                                 // temporary, decide how to break this circularity which is only caused by the KieBuilder trying to compile everything by itself.     
                                 !s.contains("DMNValidator") && !s.contains("dtanalysis"));

        Results results = kieBuilder.getResults();
        results.getMessages().forEach(System.out::println);

        InternalKieModule kieModule = (InternalKieModule) kieBuilder.getKieModule();
        List<String> generatedFiles = kieModule.getFileNames()
                                               .stream()
                                               .filter(f -> f.endsWith("java"))
                                               .collect(Collectors.toList());

        generatedFiles.forEach(System.out::println);
        System.out.println("Invoked with:" + Arrays.asList(args));

        MemoryFileSystem mfs = ((MemoryKieModule) ((CanonicalKieModule) kieModule).getInternalKieModule()).getMemoryFileSystem();
        for (String generatedFile : generatedFiles) {
            final MemoryFile f = (MemoryFile) mfs.getFile(generatedFile);
            final Path newFile = Paths.get(kieDmnValidationBaseDir.getAbsolutePath(),
                                           "target",
                                           "generated-sources",
                                           "bootstrap",
                                           f.getPath().toPortableString());

            try {
                Files.deleteIfExists(newFile);
                Files.createDirectories(newFile.getParent());
                Files.copy(f.getContents(), newFile, StandardCopyOption.REPLACE_EXISTING);

            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Unable to write file", e);
            }
        }

        byte[] droolsModelFileContent = mfs.getMap()
                                           .entrySet()
                                           .stream()
                                           .filter(kv -> kv.getKey().startsWith(CanonicalKieModule.MODEL_FILE_DIRECTORY) &&
                                                         kv.getKey().endsWith(CanonicalKieModule.MODEL_FILE_NAME))
                                           .map(Map.Entry::getValue)
                                           .findFirst()
                                           .orElseThrow(RuntimeException::new);
        List<String> lines = new BufferedReader(new StringReader(new String(droolsModelFileContent))).lines().collect(Collectors.toList());
        lines.forEach(System.out::println);
        String vbMain = new String(IoUtils.readBytesFromInputStream(ValidationBootstrapMain.class.getResourceAsStream("ValidationBootstrapModels.java")));
        String v1x = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1x.Rules")).findFirst().orElseThrow(RuntimeException::new);
        String v11 = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1_1.Rules")).findFirst().orElseThrow(RuntimeException::new);
        String v12 = lines.stream().filter(x -> x.startsWith("org.kie.dmn.validation.DMNv1_2.Rules")).findFirst().orElseThrow(RuntimeException::new);
        vbMain = vbMain.replaceAll("\\$V1X_MODEL\\$", v1x);
        vbMain = vbMain.replaceAll("\\$V11_MODEL\\$", v11);
        vbMain = vbMain.replaceAll("\\$V12_MODEL\\$", v12);
        final Path newFile = Paths.get(kieDmnValidationBaseDir.getAbsolutePath(),
                                       "target",
                                       "generated-sources",
                                       "bootstrap",
                                       "org", "kie", "dmn", "validation", "bootstrap", "ValidationBootstrapModels.java");

        try {
            Files.deleteIfExists(newFile);
            Files.createDirectories(newFile.getParent());
            Files.copy(new ByteArrayInputStream(vbMain.getBytes()), newFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to write file", e);
        }
    }
    
    public static class ASD extends CanonicalModelKieProject {

        public ASD(InternalKieModule kieModule, ClassLoader classLoader) {
            super(true, kieModule, classLoader);
        }

        @Override
        public void writeProjectOutput(MemoryFileSystem trgMfs, ResultsImpl messages) {
            System.out.println("MM wrireProjOutput");
            MemoryFileSystem srcMfs = new MemoryFileSystem();
            List<String> modelFiles = new ArrayList<>();
            ModelWriter modelWriter = new ModelWriter();
            for (ModelBuilderImpl modelBuilder : modelBuilders) {
                ModelWriter.Result result = modelWriter.writeModel(srcMfs, modelBuilder.getPackageSources());
                modelFiles.addAll(result.getModelFiles());
                final Folder sourceFolder = srcMfs.getFolder("src/main/java");
                final Folder targetFolder = trgMfs.getFolder(".");
                srcMfs.copyFolder(sourceFolder, trgMfs, targetFolder);
            }
            modelWriter.writeModelFile(modelFiles, trgMfs, getInternalKieModule().getReleaseId());
        }
    }
}

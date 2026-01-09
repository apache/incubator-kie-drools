/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.quarkus.serverless.workflow.rpc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.microprofile.config.Config;
import org.kie.kogito.serverless.workflow.rpc.FileDescriptorHolder;

import io.quarkus.bootstrap.model.ApplicationModel;
import io.quarkus.bootstrap.prebuild.CodeGenException;
import io.quarkus.deployment.CodeGenContext;
import io.quarkus.deployment.util.ProcessUtil;
import io.quarkus.maven.dependency.ResolvedDependency;
import io.quarkus.paths.PathVisit;
import io.quarkus.runtime.util.HashUtil;
import io.quarkus.utilities.OS;

public class ProtocUtils {

    /*
     * This class is a modified version of Quarkus one (https://github.com/quarkusio/quarkus/blob/main/extensions/grpc/codegen/src/main/java/io/quarkus/grpc/deployment/GrpcCodeGen.java)
     * customized to generate descriptor and avoid generating code
     * Yes, I do not like either... ;) but unfortunately the quarkus class is not extensible.
     */
    private static final String SCAN_DEPENDENCIES_FOR_PROTO = "quarkus.generate-code.grpc.scan-for-proto";
    private static final String SCAN_FOR_IMPORTS = "quarkus.generate-code.grpc.scan-for-imports";
    private static final String EXE = "exe";
    private static final String PROTO = ".proto";
    private static final String PROTOC = "protoc";
    private static final String PROTOC_GROUPID = "com.google.protobuf";

    private ProtocUtils() {
    }

    public static String from(Path path) {
        return path.normalize().toAbsolutePath().toString();
    }

    public static void generateDescriptor(Collection<Path> protoPaths, CodeGenContext context) throws CodeGenException {
        Path workDir = context.workDir();
        Path outDir = context.applicationModel().getAppArtifact().getResolvedPaths().getSinglePath().resolve(FileDescriptorHolder.DESCRIPTOR_PATH);
        Set<String> protoDirs = new HashSet<>();
        Set<String> protoFiles = new HashSet<>();
        for (Path path : protoPaths) {
            protoFiles.add(from(path));
            protoDirs.add(from(path.getParent()));
        }

        try {
            Path dirWithProtosFromDependencies = workDir.resolve("protoc-protos-from-dependencies");
            Collection<Path> protoFilesFromDependencies = gatherProtosFromDependencies(dirWithProtosFromDependencies, protoDirs,
                    context);
            if (!protoFilesFromDependencies.isEmpty()) {
                protoFilesFromDependencies.stream()
                        .map(ProtocUtils::from)
                        .forEach(protoFiles::add);
            }
            if (!protoFiles.isEmpty()) {
                Collection<String> protosToImport = gatherDirectoriesWithImports(workDir.resolve("protoc-dependencies"),
                        context);
                Executables executables = initExecutables(workDir, context.applicationModel());
                List<String> command = new ArrayList<>();
                command.add(executables.protoc.toString());
                for (String protoImportDir : protosToImport) {
                    command.add(String.format("-I=%s", escapeWhitespace(protoImportDir)));
                }
                for (String protoDir : protoDirs) {
                    command.add(String.format("-I=%s", escapeWhitespace(protoDir)));
                }

                Files.createDirectories(outDir.getParent());
                command.addAll(Arrays.asList("--plugin=protoc-gen-grpc=" + executables.grpc,
                        "--descriptor_set_out=" + outDir, "--include_imports"));
                command.addAll(protoFiles);

                ProcessBuilder processBuilder = new ProcessBuilder(command);

                final Process process = ProcessUtil.launchProcess(processBuilder, context.shouldRedirectIO());
                int resultCode = process.waitFor();
                if (resultCode != 0) {
                    throw new CodeGenException("Failed to generate descriptor from proto files: " + protoFiles +
                            " to " + outDir + " with command " + String.join(" ", command));
                }
            }
        } catch (IOException e) {
            throw new CodeGenException(
                    "Failed to generate java files from proto file in " + context.inputDir().toAbsolutePath(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new CodeGenException(
                    "Process to generate files from proto file in " + context.inputDir().toAbsolutePath() + " was interrupted", e);
        }
    }

    private static Collection<Path> gatherProtosFromDependencies(Path workDir, Set<String> protoDirectories,
            CodeGenContext context) throws CodeGenException {
        if (context.test()) {
            return Collections.emptyList();
        }
        Config properties = context.config();
        String scanDependencies = properties.getOptionalValue(SCAN_DEPENDENCIES_FOR_PROTO, String.class)
                .orElse("none");

        if ("none".equalsIgnoreCase(scanDependencies)) {
            return Collections.emptyList();
        }
        boolean scanAll = "all".equalsIgnoreCase(scanDependencies);

        List<String> dependenciesToScan = Arrays.asList(scanDependencies.split(","));

        ApplicationModel appModel = context.applicationModel();
        List<Path> protoFilesFromDependencies = new ArrayList<>();
        for (ResolvedDependency artifact : appModel.getRuntimeDependencies()) {
            if (scanAll
                    || dependenciesToScan.contains(
                            String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId()))) {
                extractProtosFromArtifact(workDir, protoFilesFromDependencies, protoDirectories, artifact);
            }
        }
        return protoFilesFromDependencies;
    }

    private static Collection<String> gatherDirectoriesWithImports(Path workDir, CodeGenContext context) throws CodeGenException {
        Config properties = context.config();

        String scanForImports = properties.getOptionalValue(SCAN_FOR_IMPORTS, String.class)
                .orElse("com.google.protobuf:protobuf-java");

        if ("none".equals(scanForImports.toLowerCase(Locale.getDefault()))) {
            return Collections.emptyList();
        }

        boolean scanAll = "all".equals(scanForImports.toLowerCase(Locale.getDefault()));
        List<String> dependenciesToScan = Arrays.asList(scanForImports.split(","));

        Set<String> importDirectories = new HashSet<>();
        ApplicationModel appModel = context.applicationModel();
        for (ResolvedDependency artifact : appModel.getRuntimeDependencies()) {
            if (scanAll
                    || dependenciesToScan.contains(
                            String.format("%s:%s", artifact.getGroupId(), artifact.getArtifactId()))) {
                extractProtosFromArtifact(workDir, new ArrayList<>(), importDirectories, artifact);
            }
        }
        return importDirectories;
    }

    private static void extractProtosFromArtifact(Path workDir, Collection<Path> protoFiles,
            Set<String> protoDirectories, ResolvedDependency artifact) throws CodeGenException {
        try {
            artifact.getContentTree().walk(
                    pathVisit -> visitPath(pathVisit, workDir, protoFiles, protoDirectories));
        } catch (GrpcCodeGenException e) {
            throw new CodeGenException(e.getMessage(), e);
        }
    }

    private static void visitPath(PathVisit pathVisit, Path workDir, Collection<Path> protoFiles, Set<String> protoDirectories) {
        Path path = pathVisit.getPath();
        if (Files.isRegularFile(path) && path.getFileName().toString().endsWith(PROTO)) {
            Path root = pathVisit.getRoot();
            if (Files.isDirectory(root)) {
                protoFiles.add(path);
                protoDirectories.add(path.getParent().normalize().toAbsolutePath().toString());
            } else { // archive
                Path relativePath = path.getRoot().relativize(path);
                Path protoUnzipDir = workDir
                        .resolve(HashUtil.sha1(root.normalize().toAbsolutePath().toString()))
                        .normalize().toAbsolutePath();
                try {
                    Files.createDirectories(protoUnzipDir);
                    protoDirectories.add(protoUnzipDir.toString());
                } catch (IOException e) {
                    throw new GrpcCodeGenException("Failed to create directory: " + protoUnzipDir, e);
                }
                Path outPath = protoUnzipDir;
                for (Path part : relativePath) {
                    outPath = outPath.resolve(part.toString());
                }
                try {
                    Files.createDirectories(outPath.getParent());
                    Files.copy(path, outPath, StandardCopyOption.REPLACE_EXISTING);
                    protoFiles.add(outPath);
                } catch (IOException e) {
                    throw new GrpcCodeGenException("Failed to extract proto file" + path + " to target: "
                            + outPath, e);
                }
            }
        }
    }

    private static String escapeWhitespace(String path) {
        if (OS.determineOS() == OS.LINUX) {
            return path.replace(" ", "\\ ");
        } else {
            return path;
        }
    }

    private static Executables initExecutables(Path workDir, ApplicationModel model) throws CodeGenException {
        Path protocPath;
        String protocPathProperty = System.getProperty("quarkus.grpc.protoc-path");
        String classifier = System.getProperty("quarkus.grpc.protoc-os-classifier", osClassifier());
        if (protocPathProperty == null) {
            protocPath = findArtifactPath(model, PROTOC_GROUPID, PROTOC, classifier, EXE);
        } else {
            protocPath = Paths.get(protocPathProperty);
        }
        Path protocExe = makeExecutableFromPath(workDir, PROTOC_GROUPID, PROTOC, classifier, "exe", protocPath);

        Path protocGrpcPluginExe = prepareExecutable(workDir, model,
                "io.grpc", "protoc-gen-grpc-java", classifier, "exe");

        return new Executables(protocExe, protocGrpcPluginExe);

    }

    private static Path prepareExecutable(Path buildDir, ApplicationModel model,
            String groupId, String artifactId, String classifier, String packaging) throws CodeGenException {
        Path artifactPath = findArtifactPath(model, groupId, artifactId, classifier, packaging);

        return makeExecutableFromPath(buildDir, groupId, artifactId, classifier, packaging, artifactPath);
    }

    private static Path makeExecutableFromPath(Path buildDir, String groupId, String artifactId, String classifier, String packaging,
            Path artifactPath) throws CodeGenException {
        Path exe = buildDir.resolve(String.format("%s-%s-%s-%s", groupId, artifactId, classifier, packaging));

        if (Files.exists(exe)) {
            return exe;
        }

        if (artifactPath == null) {
            String location = String.format("%s:%s:%s:%s", groupId, artifactId, classifier, packaging);
            throw new CodeGenException("Failed to find " + location + " among dependencies");
        }

        try {
            Files.copy(artifactPath, exe, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new CodeGenException("Failed to copy file: " + artifactPath + " to " + exe, e);
        }
        if (!exe.toFile().setExecutable(true)) {
            throw new CodeGenException("Failed to make the file executable: " + exe);
        }
        return exe;
    }

    private static Path findArtifactPath(ApplicationModel model, String groupId, String artifactId, String classifier,
            String packaging) {
        Path artifactPath = null;

        for (ResolvedDependency artifact : model.getDependencies()) {
            if (groupId.equals(artifact.getGroupId())
                    && artifactId.equals(artifact.getArtifactId())
                    && classifier.equals(artifact.getClassifier())
                    && packaging.equals(artifact.getType())) {
                artifactPath = artifact.getResolvedPaths().getSinglePath();
            }
        }
        return artifactPath;
    }

    private static String osClassifier() throws CodeGenException {
        String architecture = OS.getArchitecture();
        switch (OS.determineOS()) {
            case LINUX:
                return "linux-" + architecture;
            case WINDOWS:
                return "windows-" + architecture;
            case MAC:
                return "osx-" + architecture;
            default:
                throw new CodeGenException(
                        "Unsupported OS, please use maven plugin instead to generate Java classes from proto files");
        }
    }

    private static class Executables {

        final Path protoc;
        final Path grpc;

        Executables(Path protoc, Path grpc) {
            this.protoc = protoc;
            this.grpc = grpc;

        }
    }

    private static class GrpcCodeGenException extends RuntimeException {
        private GrpcCodeGenException(String message, Exception cause) {
            super(message, cause);
        }
    }

}

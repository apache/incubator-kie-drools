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

///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CiComputeBuildScopes {

    private static final Path DEP_GRAPH_EXTRACTOR_POM = Paths.get("script/ci/dep-graph-extractor/pom.xml");
    private static final Path DEP_GRAPH_EXTRACTOR_SRC = Paths.get("script/ci/dep-graph-extractor/src");
    private static final Path DEP_GRAPH_EXTRACTOR_JAR = Paths.get("script/ci/dep-graph-extractor/target/dep-graph-extractor-1.0.0.jar");

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("usage: jbang CiComputeBuildScopes.java <changed-out> <upstream-out> <affected-out> <changed-out>");
            System.err.println();
            System.err.println("env:");
            System.err.println("  DEP_GRAPH_EXTRACTOR__JAR               path to dep-graph-extractor jar (default: build from script/ci/dep-graph-extractor)");
            System.err.println("  DEP_GRAPH_EXTRACTOR__OUTPUT_FILE       path where the dependency graph TSV is written (default: temp file)");
            System.err.println("  DEP_GRAPH_EXTRACTOR__EXTRA_MAVEN_ARGS  whitespace-separated args forwarded to the 'mvn validate' dep-graph-extractor run (e.g. \"-Pfoo -Dbar=baz\")");
            System.err.println("  MVN                                    mvn binary (default: mvn)");
            System.exit(2);
        }

        Path fileList = Paths.get(args[0]);
        Path upstreamOut = Paths.get(args[1]);
        Path affectedOut = Paths.get(args[2]);
        Path changedOut = Paths.get(args[3]);

        Path cwd = Paths.get("").toAbsolutePath();
        if (!Files.isRegularFile(cwd.resolve("pom.xml"))) {
            System.err.println("no pom.xml in " + cwd);
            System.exit(2);
        }

        Path extractorJarPath = ensureDepGraphExtractorJar(cwd);

        String extraMavenArgsEnv = Optional.ofNullable(System.getenv("DEP_GRAPH_EXTRACTOR__EXTRA_MAVEN_ARGS")).orElse("").strip();
        List<String> extraMavenArgs = extraMavenArgsEnv.isEmpty()
                ? List.of()
                : Arrays.asList(extraMavenArgsEnv.split("\\s+"));

        // 1. map changed files -> nearest pom.xml directory (walk up recursively)
        List<Path> changedFiles = Files.readAllLines(fileList).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Paths::get)
                .map(p -> p.isAbsolute() ? p : cwd.resolve(p))
                .toList();

        // Walk up to the nearest enclosing pom.xml, but skip pom.xml files that
        // belong to nested Maven projects living under a src/ folder (e.g.
        // src/it/* invoker fixtures, src/test/resources/* test projects).
        // Those aren't part of the reactor — when they change we want the
        // enclosing reactor module to build instead.
        Set<Path> changedModuleDirs = new HashSet<>();
        for (Path f : changedFiles) {
            Path dir = Files.isDirectory(f) ? f : f.getParent();
            while (dir != null && dir.startsWith(cwd)) {
                if (Files.isRegularFile(dir.resolve("pom.xml")) && !isUnderSrcDir(cwd, dir)) {
                    changedModuleDirs.add(dir.toAbsolutePath().normalize());
                    break;
                }
                dir = dir.getParent();
            }
        }

        // 2. run dep-graph-extractor, writing dependency graph to file.
        // Persist the graph to DEP_GRAPH_EXTRACTOR__OUTPUT_FILE when set so downstream
        // tools (CiSummary) can reuse it without re-invoking Maven.
        String graphFileEnv = System.getenv("DEP_GRAPH_EXTRACTOR__OUTPUT_FILE");
        Path graphFile = (graphFileEnv != null && !graphFileEnv.isBlank())
                ? Paths.get(graphFileEnv).toAbsolutePath()
                : Files.createTempFile("dep-graph-", ".tsv");
        int rc = runMavenWithDepGraphExtractor(cwd, extractorJarPath, graphFile, extraMavenArgs);
        if (!Files.isRegularFile(graphFile) || Files.size(graphFile) == 0) {
            System.err.println("dep-graph-extractor failed (mvn rc=" + rc + ")");
            System.exit(1);
        }

        // 3. parse graph
        Map<String, Path> gaToDir = new HashMap<>();
        Map<String, Set<String>> upstreamOf = new HashMap<>();   // ga -> direct upstreams
        Map<String, Set<String>> downstreamOf = new HashMap<>(); // ga -> direct downstreams

        try (BufferedReader r = Files.newBufferedReader(graphFile)) {
            String line;
            while ((line = r.readLine()) != null) {
                String[] parts = line.split("\t", -1);
                if (parts.length < 3) continue;
                switch (parts[0]) {
                    case "P" -> {
                        gaToDir.put(parts[1], Paths.get(parts[2]).toAbsolutePath().normalize());
                        upstreamOf.computeIfAbsent(parts[1], k -> new HashSet<>());
                        downstreamOf.computeIfAbsent(parts[1], k -> new HashSet<>());
                    }
                    case "D" -> {
                        upstreamOf.computeIfAbsent(parts[1], k -> new HashSet<>()).add(parts[2]);
                        downstreamOf.computeIfAbsent(parts[2], k -> new HashSet<>()).add(parts[1]);
                    }
                }
            }
        }

        // 3b. Quarkus extension pairing: the extension-descriptor goal on a
        //     runtime module resolves its -deployment counterpart at build time.
        //     Inject a synthetic edge so both land in the same build set.
        //     This matches any <ga> / <ga>-deployment pair, not just Quarkus
        //     extensions — that is safe: the worst case is a non-extension pair
        //     gets pulled into the same set, which is conservative, not wrong.
        for (String ga : List.copyOf(gaToDir.keySet())) {
            String deploymentGa = ga + "-deployment";
            if (gaToDir.containsKey(deploymentGa)) {
                upstreamOf.computeIfAbsent(ga, k -> new HashSet<>()).add(deploymentGa);
                downstreamOf.computeIfAbsent(deploymentGa, k -> new HashSet<>()).add(ga);
            }
        }

        // 4. resolve changed dirs -> GA
        Map<Path, String> dirToGa = gaToDir.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        Set<String> changed = new HashSet<>();
        for (Path d : changedModuleDirs) {
            String ga = dirToGa.get(d);
            if (ga == null) {
                System.err.println("warn: no maven project at " + d);
                continue;
            }
            changed.add(ga);
        }

        // 5. affected = changed + transitive downstream
        Set<String> affected = traverse(changed, downstreamOf);

        // 6. upstream = transitive upstream of affected, minus affected
        Set<String> upstreamAll = traverse(affected, upstreamOf);
        upstreamAll.removeAll(affected);

        writeLines(upstreamOut, upstreamAll);
        writeLines(affectedOut, affected);
        writeLines(changedOut, changed);

        int total = gaToDir.size();
        int ignored = total - affected.size() - upstreamAll.size();
        System.out.println("total=" + total
                + " changed=" + changed.size()
                + " affected=" + affected.size()
                + " upstream=" + upstreamAll.size()
                + " ignored=" + ignored);
    }

    private static boolean isUnderSrcDir(Path cwd, Path dir) {
        Path rel = cwd.relativize(dir.toAbsolutePath().normalize());
        for (Path part : rel) {
            if ("src".equals(part.toString())) return true;
        }
        return false;
    }

    private static Set<String> traverse(Set<String> seeds, Map<String, Set<String>> edges) {
        Set<String> visited = new LinkedHashSet<>();
        Deque<String> stack = new ArrayDeque<>(seeds);
        while (!stack.isEmpty()) {
            String cur = stack.pop();
            if (visited.add(cur)) {
                Set<String> next = edges.get(cur);
                if (next != null) stack.addAll(next);
            }
        }
        return visited;
    }

    private static void writeLines(Path out, Collection<String> lines) throws IOException {
        List<String> sorted = new ArrayList<>(lines);
        Collections.sort(sorted);
        Files.write(out, sorted);
    }

    private static int runMavenWithDepGraphExtractor(Path cwd, Path extractorJar, Path graphOut,
                                                      List<String> extraArgs) throws IOException, InterruptedException {
        var cmd = new ArrayList<String>();
        cmd.add(mvnBinary());
        cmd.add("-Dmaven.ext.class.path=" + extractorJar.toAbsolutePath());
        cmd.add("-q");
        cmd.add("-DdepGraphExtractor.out=" + graphOut.toAbsolutePath());
        cmd.add("-Dorg.slf4j.simpleLogger.defaultLogLevel=error");
        cmd.addAll(extraArgs);
        cmd.add("validate");

        ProcessBuilder pb = new ProcessBuilder(cmd)
                .directory(cwd.toFile())
                .redirectErrorStream(true);
        pb.environment().put("MAVEN_OPTS",
                Optional.ofNullable(System.getenv("MAVEN_OPTS")).orElse(""));

        System.err.println("[CiComputeBuildScopes] Running: " + String.join(" ", cmd));
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                System.err.println("  " + line);
            }
        }
        return p.waitFor();
    }

    private static Path ensureDepGraphExtractorJar(Path cwd) throws IOException, InterruptedException {
        String override = System.getenv("DEP_GRAPH_EXTRACTOR__JAR");
        if (override != null && !override.isBlank()) {
            Path p = Paths.get(override).toAbsolutePath();
            if (!Files.isRegularFile(p)) {
                System.err.println("DEP_GRAPH_EXTRACTOR__JAR points to non-existent file: " + p);
                System.exit(2);
            }
            return p;
        }

        Path pom = cwd.resolve(DEP_GRAPH_EXTRACTOR_POM);
        Path jar = cwd.resolve(DEP_GRAPH_EXTRACTOR_JAR);
        Path src = cwd.resolve(DEP_GRAPH_EXTRACTOR_SRC);
        if (!Files.isRegularFile(pom)) {
            System.err.println("dep-graph-extractor pom not found: " + pom);
            System.exit(2);
        }

        if (!depGraphExtractorJarIsFresh(jar, pom, src)) {
            System.err.println("building dep-graph-extractor…");
            int rc = buildDepGraphExtractor(cwd, pom);
            if (rc != 0 || !Files.isRegularFile(jar)) {
                System.err.println("failed to build dep-graph-extractor (mvn rc=" + rc + ")");
                System.exit(1);
            }
        }
        return jar.toAbsolutePath();
    }

    private static boolean depGraphExtractorJarIsFresh(Path jar, Path pom, Path src) throws IOException {
        if (!Files.isRegularFile(jar)) return false;
        long jarMtime = Files.getLastModifiedTime(jar).toMillis();
        if (Files.getLastModifiedTime(pom).toMillis() > jarMtime) return false;
        if (!Files.isDirectory(src)) return true;
        try (Stream<Path> s = Files.walk(src)) {
            return s.filter(Files::isRegularFile).allMatch(p -> {
                try { return Files.getLastModifiedTime(p).toMillis() <= jarMtime; }
                catch (IOException e) { return false; }
            });
        }
    }

    private static int buildDepGraphExtractor(Path cwd, Path pom) throws IOException, InterruptedException {
        List<String> cmd = List.of(
                mvnBinary(),
                "--batch-mode",
                "--no-transfer-progress",
                "-f", pom.toAbsolutePath().toString(),
                "package"
        );
        ProcessBuilder pb = new ProcessBuilder(cmd)
                .directory(cwd.toFile())
                .redirectErrorStream(true);
        Process p = pb.start();
        try (BufferedReader r = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = r.readLine()) != null) {
                System.err.println("  " + line);
            }
        }
        return p.waitFor();
    }

    private static String mvnBinary() {
        // Windows ships `mvn.cmd`, not `mvn.exe` — ProcessBuilder doesn't go through
        // cmd.exe, so the bare name "mvn" fails to resolve.
        boolean windows = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
        return Optional.ofNullable(System.getenv("MVN"))
                .orElse(windows ? "mvn.cmd" : "mvn");
    }
}
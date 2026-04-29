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
package org.drools.docs;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.drools.docs.model.DecisionModelDoc;
import org.drools.docs.model.PackageDoc;
import org.drools.docs.model.RuleSetDocumentation;
import org.drools.docs.model.YardDoc;
import org.drools.docs.parser.DmnDocParser;
import org.drools.docs.parser.DocParseException;
import org.drools.docs.parser.DrlDocParser;
import org.drools.docs.parser.YamlDrlDocParser;
import org.drools.docs.parser.YardDocParser;
import org.drools.docs.renderer.DocumentRenderer;
import org.drools.docs.renderer.HtmlRenderer;
import org.drools.docs.renderer.MarkdownRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point for generating documentation from DRL, DMN, and YAML rule files.
 *
 * <p>Usage:
 * <pre>
 * RuleDocGenerator generator = RuleDocGenerator.builder()
 *     .title("My Rules")
 *     .addSource(Path.of("src/main/resources"))
 *     .build();
 * generator.generateMarkdown(Path.of("output/rules.md"));
 * generator.generateHtml(Path.of("output/rules.html"));
 * </pre>
 */
public class RuleDocGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(RuleDocGenerator.class);

    private final String title;
    private final String description;
    private final List<Path> sourcePaths;

    private final DrlDocParser drlParser = new DrlDocParser();
    private final YamlDrlDocParser yamlDrlParser = new YamlDrlDocParser();
    private final YardDocParser yardParser = new YardDocParser();
    private final DmnDocParser dmnParser = new DmnDocParser();

    private RuleDocGenerator(Builder builder) {
        this.title = builder.title;
        this.description = builder.description;
        this.sourcePaths = builder.sourcePaths;
    }

    /**
     * Scans all source paths and generates the documentation model.
     */
    public RuleSetDocumentation generate() throws IOException {
        RuleSetDocumentation doc = new RuleSetDocumentation();
        doc.setTitle(title);
        doc.setDescription(description);
        doc.setGeneratedAt(LocalDateTime.now());

        List<Path> files = collectFiles();
        doc.getSourceFiles().addAll(files);

        for (Path file : files) {
            String fileName = file.getFileName().toString().toLowerCase();
            try {
                if (fileName.endsWith(".drl.yaml") || fileName.endsWith(".drl.yml")) {
                    PackageDoc pkg = yamlDrlParser.parse(file);
                    doc.getPackages().add(pkg);
                } else if (fileName.endsWith(".drl")) {
                    PackageDoc pkg = drlParser.parse(file);
                    doc.getPackages().add(pkg);
                } else if (fileName.endsWith(".dmn")) {
                    DecisionModelDoc dmn = dmnParser.parse(file);
                    doc.getDecisionModels().add(dmn);
                } else if (isYardFile(file)) {
                    YardDoc yard = yardParser.parse(file);
                    doc.getYardDefinitions().add(yard);
                }
            } catch (DocParseException e) {
                LOG.warn("Skipping file {} due to parse error: {}", file, e.getMessage());
            } catch (Exception e) {
                LOG.warn("Skipping file {} due to unexpected error: {}", file, e.getMessage());
            }
        }

        return doc;
    }

    /**
     * Generates Markdown documentation to the given output file.
     */
    public void generateMarkdown(Path outputFile) throws IOException {
        RuleSetDocumentation doc = generate();
        new MarkdownRenderer().renderToFile(doc, outputFile);
        LOG.info("Markdown documentation written to {}", outputFile);
    }

    /**
     * Generates HTML documentation to the given output file.
     */
    public void generateHtml(Path outputFile) throws IOException {
        RuleSetDocumentation doc = generate();
        new HtmlRenderer().renderToFile(doc, outputFile);
        LOG.info("HTML documentation written to {}", outputFile);
    }

    /**
     * Generates documentation as a string in the given format.
     */
    public String generateString(OutputFormat format) throws IOException {
        RuleSetDocumentation doc = generate();
        DocumentRenderer renderer = switch (format) {
            case HTML -> new HtmlRenderer();
            case MARKDOWN -> new MarkdownRenderer();
        };
        return renderer.render(doc);
    }

    private List<Path> collectFiles() throws IOException {
        List<Path> files = new ArrayList<>();
        for (Path source : sourcePaths) {
            if (Files.isRegularFile(source)) {
                files.add(source);
            } else if (Files.isDirectory(source)) {
                Files.walkFileTree(source, new SimpleFileVisitor<>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        String name = file.getFileName().toString().toLowerCase();
                        if (name.endsWith(".drl") || name.endsWith(".dmn")
                                || name.endsWith(".drl.yaml") || name.endsWith(".drl.yml")
                                || name.endsWith(".yml") || name.endsWith(".yaml")) {
                            files.add(file);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        }
        return files;
    }

    private boolean isYardFile(Path file) throws IOException {
        String fileName = file.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".drl.yaml") || fileName.endsWith(".drl.yml")) {
            return false;
        }
        if (!fileName.endsWith(".yaml") && !fileName.endsWith(".yml")) {
            return false;
        }
        String content = Files.readString(file);
        return content.contains("kind: YaRD") || content.contains("kind: \"YaRD\"");
    }

    public enum OutputFormat { MARKDOWN, HTML }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String title = "Rule Documentation";
        private String description;
        private final List<Path> sourcePaths = new ArrayList<>();

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder addSource(Path path) {
            this.sourcePaths.add(path);
            return this;
        }

        public Builder addSources(List<Path> paths) {
            this.sourcePaths.addAll(paths);
            return this;
        }

        public RuleDocGenerator build() {
            if (sourcePaths.isEmpty()) {
                throw new IllegalArgumentException("At least one source path must be specified");
            }
            return new RuleDocGenerator(this);
        }
    }

    /**
     * CLI entry point: java -jar drools-docs-generator.jar [options] &lt;source-paths...&gt;
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        String title = "Rule Documentation";
        String description = null;
        OutputFormat format = OutputFormat.MARKDOWN;
        Path output = null;
        List<Path> sources = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--title", "-t":
                    title = args[++i];
                    break;
                case "--description", "-d":
                    description = args[++i];
                    break;
                case "--format", "-f":
                    format = OutputFormat.valueOf(args[++i].toUpperCase());
                    break;
                case "--output", "-o":
                    output = Path.of(args[++i]);
                    break;
                case "--help", "-h":
                    printUsage();
                    return;
                default:
                    sources.add(Path.of(args[i]));
            }
        }

        if (sources.isEmpty()) {
            System.err.println("Error: No source paths specified.");
            printUsage();
            System.exit(1);
        }

        if (output == null) {
            String ext = format == OutputFormat.HTML ? ".html" : ".md";
            output = Path.of("rule-documentation" + ext);
        }

        Builder builder = builder().title(title).addSources(sources);
        if (description != null) {
            builder.description(description);
        }
        RuleDocGenerator generator = builder.build();

        if (format == OutputFormat.HTML) {
            generator.generateHtml(output);
        } else {
            generator.generateMarkdown(output);
        }

        System.out.println("Documentation generated: " + output.toAbsolutePath());
    }

    private static void printUsage() {
        System.out.println("""
                Usage: drools-docs-generator [options] <source-paths...>

                Generates human-readable documentation from DRL, DMN, and YAML rule files.

                Options:
                  -t, --title <title>         Document title (default: "Rule Documentation")
                  -d, --description <desc>    Document description
                  -f, --format <format>       Output format: MARKDOWN or HTML (default: MARKDOWN)
                  -o, --output <file>         Output file path
                  -h, --help                  Show this help

                Examples:
                  drools-docs-generator src/main/resources/rules/
                  drools-docs-generator -f HTML -o docs/rules.html src/main/resources/
                  drools-docs-generator -t "Loan Rules" rules.drl decisions.dmn
                """);
    }
}

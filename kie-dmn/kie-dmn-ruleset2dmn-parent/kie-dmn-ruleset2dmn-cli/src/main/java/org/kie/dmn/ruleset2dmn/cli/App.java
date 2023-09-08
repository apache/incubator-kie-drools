/**
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
package org.kie.dmn.ruleset2dmn.cli;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import org.kie.dmn.ruleset2dmn.Converter;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "java -jar <ruleset2dmn-cli .jar file>",
        mixinStandardHelpOptions = true,
        versionProvider = RuleSet2DMNVersionProvider.class,
        description = "Experimental DMN generator for PMML RuleSet models to be converted to DMN decision tables.")
public class App implements Callable<Integer> {
    @Option(names = { "-o", "--output" }, paramLabel = "OUTPUT_FILE", description = "The output DMN file to be created containing the converted to DMN decision tables. If left empty, the generated DMN xml will be emitted on STDOUT.")
    File archive;

    @Parameters(index = "0", paramLabel = "INPUT_FILE", description = "The input PMML RuleSet model file to be converted to DMN decision tables. If left empty, will read from STDIN.", arity = "0..1")
    private File inputFile;
    private InputStream inputStream;

    @Override
    public Integer call() throws Exception {
        initInputStream();
        final String modelName = inputFile != null ? inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".")) : "RuleInductionModel";
        String xml = Converter.parse(modelName, inputStream);
        Consumer<String> outStrategy = archive != null ? new FileWriteStrategy(archive) : System.out::println;
        outStrategy.accept(xml);
        return 0;
    }

    private static class FileWriteStrategy implements Consumer<String> {
        private File f;
        FileWriteStrategy(File f) {
            this.f = f;
        }
        @Override
        public void accept(String t) {
            try {
                Files.write(f.toPath(), t.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    /**
     * Init file content from STDIN if necessary.
     */
    private void initInputStream() throws FileNotFoundException {
        if (inputFile == null) {
            try (Scanner scanner = new Scanner(System.in).useDelimiter("\\A")) {
                if (scanner.hasNext()) {
                    inputStream = new ByteArrayInputStream(scanner.next().getBytes());
                }
            }
        } else {
            if (!inputFile.exists()) {
                throw new IllegalArgumentException(inputFile + " does not exists.");
            }
            inputStream = new FileInputStream(inputFile);
        }
    }
}

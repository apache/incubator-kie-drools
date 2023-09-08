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
package org.kie.dmn.xls2dmn.cli;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "java -jar <xl2dmn .jar file>",
        mixinStandardHelpOptions = true,
        versionProvider = XLS2DMNVersionProvider.class,
        description = "Experimental DMN generator for Excel (.xls/.xlsx) file containing DMN decision tables.")
public class App implements Callable<Integer> {
    @Parameters(index = "0", paramLabel = "INPUT_FILE", description = "The input Excel (.xls/.xlsx) file containing DMN decision tables.")
    private File inputFile;
    @Parameters(index = "1", paramLabel = "OUTPUT_FILE", arity = "0..1", description = {"Specify filename for generated DMN model file.",
                                                                                        "If not specified, will generate INPUT_FILE with .dmn postfixed."})
    private File outputFile;

    @Override
    public Integer call() throws Exception { 
        if (!inputFile.exists()) {
            throw new RuntimeException(inputFile + " does not exists.");
        }
        if (outputFile == null) {
            outputFile = new File(inputFile.getAbsolutePath() +".dmn");
        } 
        System.out.println("Using inputFile: "+inputFile.getAbsolutePath());
        System.out.println("Using outputFile: "+outputFile.getAbsolutePath());
        new XLS2DMNParser(outputFile).parseFile(inputFile);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
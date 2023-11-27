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
package org.drools.drlonyaml.cli;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.drools.drlonyaml.model.DrlPackage;
import org.drools.drlonyaml.todrl.YAMLtoDrlDumper;
import org.drools.util.IoUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import static org.drools.drlonyaml.cli.utils.Utils.conventionInputStream;
import static org.drools.drlonyaml.cli.utils.Utils.conventionOutputConsumer;
import static org.drools.drlonyaml.model.Utils.getYamlMapper;

/**
 * Note: beyond different annotations, Parameters and Options are managed per subcommand,
 * in order to have them listed after the specific subcommand on the CLI.
 */
@Command(name="yaml2drl", description="Converts a single .yml file to DRL format.")
public class Yaml2Drl implements Callable<Integer> {
    
    @Option(names = { "-o", "--output" }, paramLabel = "OUTPUT_FILE", description = "The output file to write the content of the conversion to. If left empty, the translated format will be emitted on STDOUT.")
    private File archive;

    @Parameters(index = "0", paramLabel = "INPUT_FILE", description = "The .yml file to be converted in YAML format. If left empty, content will be read from STDIN.", arity = "0..1")
    private File inputFile;
    private InputStream inputStream;
    
    @Override
    public Integer call() throws Exception {
        inputStream = conventionInputStream(inputFile);
        String content = new String(IoUtils.readBytesFromInputStream(inputStream));
        final String drlText = yaml2drl(content);
        conventionOutputConsumer(archive).accept(drlText);
        return 0;
    }
    
    public static String yaml2drl(String yaml) throws Exception {
        DrlPackage readValue = getYamlMapper().readValue(yaml, DrlPackage.class);
        final String drlText = YAMLtoDrlDumper.dumpDRL(readValue);
        return drlText;
    }
}

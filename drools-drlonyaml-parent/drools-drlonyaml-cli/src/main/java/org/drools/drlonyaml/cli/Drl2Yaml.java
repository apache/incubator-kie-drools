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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.Callable;

import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DrlParser;
import org.drools.drlonyaml.model.DrlPackage;
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
@Command(name="drl2yaml", description="Converts a single .drl file to YAML format.")
public class Drl2Yaml implements Callable<Integer> {
    
    @Option(names = { "-o", "--output" }, paramLabel = "OUTPUT_FILE", description = "The output file to write the content of the conversion to. If left empty, the translated format will be emitted on STDOUT.")
    private File archive;

    @Parameters(index = "0", paramLabel = "INPUT_FILE", description = "The .drl file to be converted in YAML format. If left empty, content will be read from STDIN.", arity = "0..1")
    private File inputFile;
    private InputStream inputStream;
    
    private static final DrlParser drlParser = new DrlParser();

    @Override
    public Integer call() throws Exception {
        inputStream = conventionInputStream(inputFile);
        String content = new String(IoUtils.readBytesFromInputStream(inputStream));
        final String yaml = drl2yaml(content);
        conventionOutputConsumer(archive).accept(yaml);
        return 0;
    }
    
    public static String drl2yaml(String drl) throws Exception {
        PackageDescr pkgDescr = drlParser.parse(new StringReader(drl));
        DrlPackage model = DrlPackage.from(pkgDescr);
        StringWriter writer = new StringWriter();
        getYamlMapper().writeValue(writer, model);
        final String yaml = writer.toString();
        writer.close();
        return yaml;
    }
}

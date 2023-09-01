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

import org.drools.drlonyaml.cli.utils.DrlOnYamlCliVersionProvider;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

@Command(mixinStandardHelpOptions = true,
    name = "java -jar <drools-drlonyaml-cli .jar file>",
    scope = ScopeType.INHERIT,
    versionProvider = DrlOnYamlCliVersionProvider.class,
    subcommands = { Drl2Yaml.class, Yaml2Drl.class, Batch2Drl.class, Batch2Yaml.class })
public class App {
    
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        if (exitCode != 0) {
            System.exit(exitCode);            
        }
    }
}

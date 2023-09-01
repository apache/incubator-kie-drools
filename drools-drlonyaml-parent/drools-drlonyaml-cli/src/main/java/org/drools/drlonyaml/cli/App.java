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

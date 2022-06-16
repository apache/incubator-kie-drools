package org.kie.dmn.ruleset2dmn.cli;

import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.Callable;

import org.kie.dmn.ruleset2dmn.Converter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "java -jar <ruleset2dmn-cli .jar file>",
        mixinStandardHelpOptions = true,
        versionProvider = RuleSet2DMNVersionProvider.class,
        description = "Experimental DMN generator for PMML RuleSet models to be converted to DMN decision tables.")
public class App implements Callable<Integer> {
    @Parameters(index = "0", paramLabel = "INPUT_FILE", description = "The input PMML RuleSet model file to be converted to DMN decision tables.")
    private File inputFile;

    @Override
    public Integer call() throws Exception { 
        if (!inputFile.exists()) {
            throw new RuntimeException(inputFile + " does not exists.");
        }
        String xml = Converter.parse(inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".")), new FileInputStream(inputFile));
        System.out.println(xml);
        return 0;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }
}
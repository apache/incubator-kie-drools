package org.kie.dmn.xls2dmn.cli;

import picocli.CommandLine;

/**
 * Internal utility class not meant to be invoked from CLI, but to be used in the same-VM and same-process
 */
public class SameVMApp extends App {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new SameVMApp()).execute(args);
        if (exitCode != 0) {
            throw new RuntimeException(SameVMApp.class.getCanonicalName() + " failed conversion");
        }
    }
}
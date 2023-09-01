package org.drools.drlonyaml.cli.utils;

import org.drools.base.util.Drools;

import picocli.CommandLine.IVersionProvider;

public class DrlOnYamlCliVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[]{Drools.getFullVersion()};
    }

}

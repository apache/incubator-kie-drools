package org.kie.dmn.xls2dmn.cli;

import org.drools.base.util.Drools;

import picocli.CommandLine.IVersionProvider;

public class XLS2DMNVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[]{Drools.getFullVersion()};
    }

}

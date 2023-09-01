package org.kie.dmn.ruleset2dmn.cli;

import org.drools.base.util.Drools;
import picocli.CommandLine.IVersionProvider;

public class RuleSet2DMNVersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() throws Exception {
        return new String[]{Drools.getFullVersion()};
    }

}

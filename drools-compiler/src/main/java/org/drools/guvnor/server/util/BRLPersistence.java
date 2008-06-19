package org.drools.guvnor.server.util;

import org.drools.guvnor.client.modeldriven.brl.RuleModel;

public interface BRLPersistence {

    public String marshal(final RuleModel model);
    public RuleModel unmarshal(final String str);
}
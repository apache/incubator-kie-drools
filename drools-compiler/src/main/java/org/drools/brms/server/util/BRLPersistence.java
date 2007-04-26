package org.drools.brms.server.util;

import org.drools.brms.client.modeldriven.brxml.RuleModel;

public interface BRLPersistence {

    public String marshal(final RuleModel model);

    public RuleModel unmarshal(final String str);

}
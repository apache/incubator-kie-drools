package org.drools.rule.builder.dialect.clips;

import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;

public class ClipsDialect extends MVELDialect {
    
    private static final ClipsConsequenceBuilder          consequence                 = new ClipsConsequenceBuilder();
    
    public final static String ID = "clips";

    public ClipsDialect() {
        super();
    }

    public String getId() {
        return ID;
    }
    
    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }    
}

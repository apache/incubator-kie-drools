package org.drools.rule.builder.dialect.clips;

import java.util.ArrayList;

import org.drools.commons.jci.readers.MemoryResourceReader;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageRegistry;
import org.drools.rule.Package;
import org.drools.rule.builder.ConsequenceBuilder;
import org.drools.rule.builder.ReturnValueBuilder;
import org.drools.rule.builder.RuleConditionBuilder;
import org.drools.rule.builder.dialect.mvel.MVELDialect;
import org.drools.rule.builder.dialect.mvel.MVELDialectConfiguration;

public class ClipsDialect extends MVELDialect {

    private static final ClipsConsequenceBuilder consequence = new ClipsConsequenceBuilder();
    private static final ClipsEvalBuilder        eval        = new ClipsEvalBuilder();
    private static final ClipsReturnValueBuilder returnValue = new ClipsReturnValueBuilder();

    public final static String                   ID          = "clips";

    public ClipsDialect(PackageBuilder builder,
                                   PackageRegistry pkgRegistry,
                                   Package pkg) {
        super( builder, pkgRegistry, pkg, ID);
        setLanguageLevel( 5 );

    }

    public String getId() {
        return ID;
    }

    public ConsequenceBuilder getConsequenceBuilder() {
        return this.consequence;
    }
    
    public RuleConditionBuilder getEvalBuilder() {
        return this.eval;
    }
    
    public ReturnValueBuilder getReturnValueBuilder() {
        return this.returnValue;
    }   

}

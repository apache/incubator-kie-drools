package org.drools.modelcompiler.builder;

import org.drools.compiler.lang.descr.RuleDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RuleDescrImpl {
    private RuleDescr descr;
    private RuleImpl impl;
    public RuleDescrImpl(RuleDescr descr, RuleImpl impl) {
        super();
        this.descr = descr;
        this.impl = impl;
    }
    
    public RuleDescr getDescr() {
        return descr;
    }
    
    public RuleImpl getImpl() {
        return impl;
    }
    
}

package org.drools.definitions.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Rule;
import org.drools.definitions.rule.impl.RuleImpl;
import org.drools.rule.Package;

public class KnowledgePackageImp
    implements
    KnowledgePackage,
    Externalizable {
    public Package pkg;

    public KnowledgePackageImp() {
        this.pkg = null;
    }

    public KnowledgePackageImp(Package pkg) {
        this.pkg = pkg;
    }

    public String getName() {
        return this.pkg.getName();
    }

    public Collection<Rule> getRules() {
        org.drools.rule.Rule[] rules = pkg.getRules();
        List<Rule> list = new ArrayList<Rule>( rules.length );
        for ( org.drools.rule.Rule rule : rules ) {
            list.add( new RuleImpl( rule ) );
        }
        return list;
    }

    public Collection<Process> getProcesses() {
        Collection<org.drools.definition.process.Process> processes = (Collection<org.drools.definition.process.Process>) pkg.getRuleFlows().values();
        List<Process> list = new ArrayList<Process>( processes.size() );
        for ( org.drools.definition.process.Process process : processes ) {
            list.add( process );
        }
        return list;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        this.pkg = new Package();
        this.pkg.readExternal( in );
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        this.pkg.writeExternal( out );
    }


}

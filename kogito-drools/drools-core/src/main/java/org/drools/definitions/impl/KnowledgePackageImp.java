/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import org.drools.rule.Function;
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

    /**
     * Delegate method to retrieve a Rule by its name.
     * @param name the rule's name
     * @return
     * @see org.drools.rule.Package#getRule(java.lang.String)
     */
    public Rule getRule(String name) {
        return this.pkg.getRule(name);
    }

    /**
     * Delegate method to remove a Rule by its name.
     * @param rule the rule to be removed
     * @return
     * @see org.drools.rule.Package#removeRule(org.drools.rule.Rule) 
     */
    public void removeRule(org.drools.rule.Rule rule) {
        pkg.removeRule(rule);
    }

    /**
     * Delegate method to retrieve a Function by its name.
     * @param name the function's name
     * @return
     * @see org.drools.rule.Package#getFunctions()
     */
    public Function getFunction(String name) {
        return this.pkg.getFunctions().containsKey(name)?this.pkg.getFunctions().get(name):null;
    }

    /**
     * Delegate method to retrieve a Rule by its name.
     * @param functionName the function's name
     * @return
     * @see org.drools.rule.Package#removeFunction(java.lang.String) 
     */
    public void removeFunction(String functionName) {
        pkg.removeFunction(functionName);
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



    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        KnowledgePackageImp that = (KnowledgePackageImp) o;

        if (pkg == null || that.pkg == null) {
            return false;
        }
        // JBRULES-3143
        // Fixing the KnowledgeAgent: need to distinguish cases when the agent would try
        // to load the same package from cases where a package is built from multiple resources
        // (i.e. two partial packages with the same name are returned).
        // Package equality would just check the name, so use identity
        return pkg == that.pkg;
    }


    public int hashCode() {
        return pkg != null ? pkg.hashCode() : 0;
    }
}

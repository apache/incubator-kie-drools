/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.definition.process.Process;
import org.drools.definition.rule.Global;
import org.drools.definition.rule.Query;
import org.drools.definition.rule.Rule;
import org.drools.definition.type.FactType;
import org.kie.internal.definition.KnowledgePackage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.drools.impl.adapters.FactTypeAdapter.adaptFactTypes;
import static org.drools.impl.adapters.GlobalAdapter.adaptGlobals;
import static org.drools.impl.adapters.ProcessAdapter.adaptProcesses;
import static org.drools.impl.adapters.QueryAdapter.adaptQueries;
import static org.drools.impl.adapters.RuleAdapter.adaptRules;

public class KnowledgePackageAdapter implements org.drools.definition.KnowledgePackage {

    private final KnowledgePackage delegate;

    public KnowledgePackageAdapter(KnowledgePackage delegate) {
        this.delegate = delegate;
    }

    public String getName() {
        return delegate.getName();
    }

    public Collection<Rule> getRules() {
        return adaptRules(delegate.getRules());
    }

    public Collection<Process> getProcesses() {
        return adaptProcesses(delegate.getProcesses());
    }

    public Collection<FactType> getFactTypes() {
        return adaptFactTypes(delegate.getFactTypes());
    }

    public Collection<Query> getQueries() {
        return adaptQueries(delegate.getQueries());
    }

    public Collection<String> getFunctionNames() {
        return delegate.getFunctionNames();
    }

    public Collection<Global> getGlobalVariables() {
        return adaptGlobals(delegate.getGlobalVariables());
    }

    public KnowledgePackage getDelegate() {
        return delegate;
    }

    public static List<org.drools.definition.KnowledgePackage> adaptKnowledgePackages(Collection<KnowledgePackage> pkgs) {
        List<org.drools.definition.KnowledgePackage> result = new ArrayList<org.drools.definition.KnowledgePackage>();
        for (KnowledgePackage pkg : pkgs) {
            result.add(new KnowledgePackageAdapter(pkg));
        }
        return result;
    }

    public static Collection<KnowledgePackage> fromKiePackages(Collection<org.drools.definition.KnowledgePackage> pkgs) {
        List<KnowledgePackage> result = new ArrayList<KnowledgePackage>();
        for (org.drools.definition.KnowledgePackage pkg : pkgs) {
            result.add(((KnowledgePackageAdapter)pkg).getDelegate());
        }
        return result;
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgePackageAdapter && delegate.equals(((KnowledgePackageAdapter)obj).delegate);
    }
}

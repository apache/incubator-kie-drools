package org.drools.impl.adapters;

import org.drools.definition.KnowledgePackage;
import org.drools.definition.process.Process;
import org.drools.definition.rule.Query;
import org.drools.definition.rule.Rule;
import org.drools.definition.type.FactType;
import org.drools.event.knowledgebase.KnowledgeBaseEventListener;
import org.drools.runtime.Environment;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.internal.KnowledgeBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.drools.impl.adapters.KnowledgePackageAdapter.adaptKnowledgePackages;
import static org.drools.impl.adapters.KnowledgePackageAdapter.fromKiePackages;
import static org.drools.impl.adapters.ProcessAdapter.adaptProcesses;
import static org.drools.impl.adapters.StatefulKnowledgeSessionAdapter.adaptStatefulKnowledgeSession;

public class KnowledgeBaseAdapter implements org.drools.KnowledgeBase, Externalizable {

    public KnowledgeBase delegate;

    private final Map<KnowledgeBaseEventListener, KieBaseEventListener> listeners = new HashMap<KnowledgeBaseEventListener, KieBaseEventListener>();

    public KnowledgeBaseAdapter(KnowledgeBase delegate) {
        this.delegate = delegate;
    }

    public void addKnowledgePackages(Collection<KnowledgePackage> kpackages) {
        delegate.addKnowledgePackages(fromKiePackages(kpackages));
    }

    public Collection<KnowledgePackage> getKnowledgePackages() {
        return adaptKnowledgePackages(delegate.getKnowledgePackages());
    }

    public KnowledgePackage getKnowledgePackage(String packageName) {
        return new KnowledgePackageAdapter(delegate.getKnowledgePackage(packageName));
    }

    public void removeKnowledgePackage(String packageName) {
        delegate.removeKnowledgePackage(packageName);
    }

    public Rule getRule(String packageName, String ruleName) {
        return new RuleAdapter(delegate.getRule(packageName, ruleName));
    }

    public void removeRule(String packageName, String ruleName) {
        delegate.removeRule(packageName, ruleName);
    }

    public Query getQuery(String packageName, String queryName) {
        return new QueryAdapter(delegate.getQuery(packageName, queryName));
    }

    public void removeQuery(String packageName, String queryName) {
        delegate.removeQuery(packageName, queryName);
    }

    public void removeFunction(String packageName, String ruleName) {
        delegate.removeFunction(packageName, ruleName);
    }

    public FactType getFactType(String packageName, String typeName) {
        return new FactTypeAdapter(delegate.getFactType(packageName, typeName));
    }

    public org.drools.definition.process.Process getProcess(String processId) {
        return new ProcessAdapter(delegate.getProcess(processId));
    }

    public void removeProcess(String processId) {
        delegate.removeProcess(processId);
    }

    public Collection<Process> getProcesses() {
        return adaptProcesses(delegate.getProcesses());
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession(KnowledgeSessionConfiguration conf, Environment environment) {
        return new StatefulKnowledgeSessionAdapter(delegate.newStatefulKnowledgeSession(conf == null ? null : ((KnowledgeSessionConfigurationAdapter)conf).getDelegate(),
                                                                                        environment == null ? null : ((EnvironmentAdapter)environment).getDelegate()));
    }

    public StatefulKnowledgeSession newStatefulKnowledgeSession() {
        return new StatefulKnowledgeSessionAdapter(delegate.newStatefulKnowledgeSession());
    }

    public Collection<StatefulKnowledgeSession> getStatefulKnowledgeSessions() {
        return adaptStatefulKnowledgeSession(delegate.getStatefulKnowledgeSessions());
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession(KnowledgeSessionConfiguration conf) {
        return new StatelessKnowledgeSessionAdapter(delegate.newStatelessKnowledgeSession(conf == null ? null : ((KnowledgeSessionConfigurationAdapter)conf).getDelegate()));
    }

    public StatelessKnowledgeSession newStatelessKnowledgeSession() {
        return new StatelessKnowledgeSessionAdapter(delegate.newStatelessKnowledgeSession());
    }

    public Set<String> getEntryPointIds() {
        return delegate.getEntryPointIds();
    }

    public void addEventListener(KnowledgeBaseEventListener listener) {
        KnowledgeBaseEventListenerAdapter adapted = new KnowledgeBaseEventListenerAdapter(listener);
        listeners.put(listener, adapted);
        delegate.addEventListener(adapted);
    }

    public void removeEventListener(KnowledgeBaseEventListener listener) {
        delegate.removeEventListener(listeners.remove(listener));
    }

    public Collection<KnowledgeBaseEventListener> getKnowledgeBaseEventListeners() {
        return listeners.keySet();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof KnowledgeBaseAdapter && delegate.equals(((KnowledgeBaseAdapter)obj).delegate);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(delegate);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        delegate = (KnowledgeBase) in.readObject();
    }
}

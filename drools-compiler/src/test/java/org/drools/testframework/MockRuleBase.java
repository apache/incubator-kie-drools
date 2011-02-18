package org.drools.testframework;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.List;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.definition.type.FactType;
import org.drools.event.RuleBaseEventListener;
import org.drools.marshalling.Marshaller;
import org.drools.rule.Package;
import org.drools.runtime.Environment;

public class MockRuleBase implements RuleBase {

    public int getAdditionsSinceLock() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Package getPackage(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Package[] getPackages() {

        return new Package[0];
    }

    public int getRemovalsSinceLock() {
        // TODO Auto-generated method stub
        return 0;
    }

    public StatefulSession[] getStatefulSessions() {
        // TODO Auto-generated method stub
        return null;
    }

    public void lock() {
        // TODO Auto-generated method stub

    }

    public StatefulSession newStatefulSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public StatefulSession newStatefulSession(SessionConfiguration config, Environment environment ) {
        // TODO Auto-generated method stub
        return null;
    }

    public StatefulSession readStatefulSession(InputStream stream,
            boolean keepReference) throws IOException, ClassNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public StatelessSession newStatelessSession() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeFunction(String packageName, String functionName) {
        // TODO Auto-generated method stub

    }

    public void removePackage(String packageName) {
        // TODO Auto-generated method stub

    }

    public void removeProcess(String id) {
        // TODO Auto-generated method stub

    }

    public void removeRule(String packageName, String ruleName) {
        // TODO Auto-generated method stub

    }

    public void unlock() {
        // TODO Auto-generated method stub

    }

    public void addEventListener(RuleBaseEventListener listener) {
        // TODO Auto-generated method stub

    }

    public List getRuleBaseEventListeners() {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeEventListener(RuleBaseEventListener listener) {
        // TODO Auto-generated method stub

    }

    public void addPackage(Package pkg) {
        // TODO Auto-generated method stub

    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public void writeStatefulSession(StatefulSession session,
                                     OutputStream stream) throws IOException {
        // TODO Auto-generated method stub
        
    }

    public StatefulSession readStatefulSession(InputStream stream,
                                               Marshaller marshaller) throws IOException,
                                                                     ClassNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public StatefulSession readStatefulSession(InputStream stream,
                                               boolean keepReference,
                                               Marshaller marshaller,
                                               SessionConfiguration config,
                                               Environment environment) throws IOException,
                                                                     ClassNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    public void writeStatefulSession(StatefulSession session,
                                     OutputStream stream,
                                     Marshaller marshaller) throws IOException {
        // TODO Auto-generated method stub
        
    }

    public FactType getFactType(String string) {
        // TODO Auto-generated method stub
        return null;
    }

    public void addPackages(Package[] pkgs) {
        // TODO Auto-generated method stub
        
    }

    public StatefulSession newStatefulSession(boolean keepReference) {
        // TODO Auto-generated method stub
        return null;
    }

    public StatefulSession newStatefulSession(InputStream stream,
                                              boolean keepReference) {
        // TODO Auto-generated method stub
        return null;
    }

    public StatefulSession newStatefulSession(InputStream stream) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeQuery(String packageName,
                            String queryName) {
        // TODO Auto-generated method stub
        
    }
}

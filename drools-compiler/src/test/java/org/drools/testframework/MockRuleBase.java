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
        return 0;
    }

    public Package getPackage(String name) {
        return null;
    }

    public Package[] getPackages() {
        return new Package[0];
    }

    public int getRemovalsSinceLock() {
        return 0;
    }

    public StatefulSession[] getStatefulSessions() {
        return null;
    }

    public void lock() {
    }

    public StatefulSession newStatefulSession() {
        return null;
    }

    public StatefulSession newStatefulSession(SessionConfiguration config, Environment environment ) {
        return null;
    }

    public StatefulSession readStatefulSession(InputStream stream,
                                               boolean keepReference) throws IOException, ClassNotFoundException {
        return null;
    }
    
    public StatefulSession readStatefulSession(InputStream stream,
                                               boolean keepReference,
                                               SessionConfiguration config) throws IOException, ClassNotFoundException {
        return null;
    }    

    public StatelessSession newStatelessSession() {
        return null;
    }

    public void removeFunction(String packageName, String functionName) {
    }

    public void removePackage(String packageName) {
    }

    public void removeProcess(String id) {
    }

    public void removeRule(String packageName, String ruleName) {
    }

    public void unlock() {
    }

    public void addEventListener(RuleBaseEventListener listener) {
    }

    public List getRuleBaseEventListeners() {
        return null;
    }

    public void removeEventListener(RuleBaseEventListener listener) {
    }

    public void addPackage(Package pkg) {
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {

    }

    public void writeExternal(ObjectOutput out) throws IOException {

    }

    public void writeStatefulSession(StatefulSession session,
                                     OutputStream stream) throws IOException {
    }

    public StatefulSession readStatefulSession(InputStream stream,
                                               Marshaller marshaller) throws IOException,
                                                                     ClassNotFoundException {
        return null;
    }

    public StatefulSession readStatefulSession(InputStream stream,
                                               boolean keepReference,
                                               Marshaller marshaller,
                                               SessionConfiguration config,
                                               Environment environment) throws IOException,
                                                                     ClassNotFoundException {
        return null;
    }

    public void writeStatefulSession(StatefulSession session,
                                     OutputStream stream,
                                     Marshaller marshaller) throws IOException {
    }

    public FactType getFactType(String string) {
        return null;
    }

    public void addPackages(Package[] pkgs) {
    }

    public StatefulSession newStatefulSession(boolean keepReference) {
        return null;
    }

    public StatefulSession newStatefulSession(InputStream stream,
                                              boolean keepReference) {
        return null;
    }

    public StatefulSession newStatefulSession(InputStream stream) {
        return null;
    }

    public void removeQuery(String packageName,
                            String queryName) {
    }

    public StatefulSession newStatefulSession(InputStream stream,
                                              boolean keepReference,
                                              SessionConfiguration conf) {
        return null;
    }
}

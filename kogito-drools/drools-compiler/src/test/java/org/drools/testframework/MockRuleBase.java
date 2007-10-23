package org.drools.testframework;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.event.RuleBaseEventListener;
import org.drools.rule.Package;

public class MockRuleBase implements RuleBase {

	public void addPackage(Package pkg) throws Exception {
		// TODO Auto-generated method stub

	}

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

	public StatefulSession newStatefulSession(boolean keepReference) {
		// TODO Auto-generated method stub
		return null;
	}

	public StatefulSession newStatefulSession(InputStream stream)
			throws IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public StatefulSession newStatefulSession(InputStream stream,
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

}

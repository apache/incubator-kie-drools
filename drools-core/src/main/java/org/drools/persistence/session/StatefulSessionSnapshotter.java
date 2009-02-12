package org.drools.persistence.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallingConfiguration;
import org.drools.persistence.ByteArraySnapshotter;

public class StatefulSessionSnapshotter implements ByteArraySnapshotter<StatefulSession> {
	
	private RuleBase ruleBase;
	private SessionConfiguration conf;
	private StatefulSession session;
	private Marshaller marshaller;

	public StatefulSessionSnapshotter(RuleBase ruleBase) {
		this(ruleBase, null);
	}

	public StatefulSessionSnapshotter(RuleBase ruleBase, MarshallingConfiguration marshallingConfiguration) {
		this(ruleBase, new SessionConfiguration(), marshallingConfiguration);
	}

	public StatefulSessionSnapshotter(RuleBase ruleBase, SessionConfiguration conf, MarshallingConfiguration marshallingConfiguration) {
		this.ruleBase = ruleBase;
		this.conf = conf;
		this.marshaller = new DefaultMarshaller(((InternalRuleBase) ruleBase).getConfiguration(), marshallingConfiguration);
	}

	public StatefulSessionSnapshotter(StatefulSession session) {
		this(session, null);
	}

	public StatefulSessionSnapshotter(StatefulSession session, MarshallingConfiguration marshallingConfiguration) {
		this.session = session;
		this.ruleBase = session.getRuleBase();
		this.marshaller = new DefaultMarshaller(((InternalRuleBase) ruleBase).getConfiguration(), marshallingConfiguration);
	}

	public byte[] getSnapshot() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			marshaller.write(baos, (InternalRuleBase) ruleBase, session);
		} catch (IOException e) {
			throw new RuntimeException("Unable to get session snapshot", e);
		}

		return baos.toByteArray();
	}

	public void loadSnapshot(byte[] bytes) {
		if (session == null) {
			session = ruleBase.newStatefulSession(conf, null);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		try {
			marshaller.read(bais, (InternalRuleBase) ruleBase, session);
		} catch (Exception e) {
			throw new RuntimeException("Unable to load session snapshot", e);
		}
	}

	public StatefulSession getObject() {
		return session;
	}

}

package org.drools.persistence.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.common.InternalRuleBase;
import org.drools.marshalling.DefaultMarshaller;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.PlaceholderResolverStrategyFactory;
import org.drools.persistence.ByteArraySnapshotter;

public class StatefulSessionSnapshotter implements ByteArraySnapshotter<StatefulSession> {
	
	private RuleBase ruleBase;
	private StatefulSession session;
	private Marshaller marshaller;

	private StatefulSessionSnapshotter(StatefulSession session, RuleBase ruleBase, PlaceholderResolverStrategyFactory factory) {
		this.session = session;
		this.ruleBase = ruleBase;
		this.marshaller = new DefaultMarshaller(null, factory);
	}

	public StatefulSessionSnapshotter(RuleBase ruleBase) {
		this(null, ruleBase, null);
	}

	public StatefulSessionSnapshotter(RuleBase ruleBase, PlaceholderResolverStrategyFactory factory) {
		this(null, ruleBase, factory);
	}

	public StatefulSessionSnapshotter(StatefulSession session) {
		this(session, session.getRuleBase(), null);
	}

	public StatefulSessionSnapshotter(StatefulSession session, PlaceholderResolverStrategyFactory factory) {
		this(session, session.getRuleBase(), factory);
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
			session = ruleBase.newStatefulSession();
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

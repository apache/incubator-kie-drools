package org.drools.ruleflow.instance.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.ruleflow.core.Node;

public class PvmNodeRegistry {
	private Map<Class<? extends Node>, PvmNodeConf> registry;
	
	public PvmNodeRegistry() {
		this.registry = new HashMap<Class<? extends Node>, PvmNodeConf>();
	}
	
	public void  register(Class<? extends Node> cls, PvmNodeConf conf) {
		this.registry.put(cls, conf);
	}
	
	public PvmNodeConf getRuleFlowNodeConf(Node node) {
		return this.registry.get( node.getClass() );
	}
}

package org.drools.definitions.rule.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.rule.Rule;

public class RuleImpl implements org.drools.definition.rule.Rule {
	private Rule rule;
	
	public RuleImpl(Rule rule) {
		this.rule = rule;
	}

	public String getName() {
		return this.rule.getName();
	}
	
	public String getPackageName() {
		return this.rule.getPackage();
	}
	
	@Deprecated
	public String getMetaAttribute(String identifier) {
	    return this.rule.getMetaAttribute( identifier );
	}

	@Deprecated
    public Collection<String> listMetaAttributes() {
        return this.rule.getMetaAttributes().keySet();
    }

    @Deprecated
    public Map<String, Object> getMetaAttributes() {
        return this.rule.getMetaAttributes();
    }

    public Map<String, Object> getMetaData() {
        return this.rule.getMetaData();
    }
	
	
}

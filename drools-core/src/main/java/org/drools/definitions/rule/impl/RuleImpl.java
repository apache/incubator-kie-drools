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

package org.drools.definitions.rule.impl;

import java.util.Collection;
import java.util.Map;

import org.drools.definition.rule.Query;
import org.drools.rule.Rule;

public class RuleImpl implements org.drools.definition.rule.Rule, Query {
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
    
    public Rule getRule() {
    	return rule;
    }

    public int hashCode() {
        return ((rule == null) ? 37 : rule.hashCode());
    }

    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        RuleImpl other = (RuleImpl) obj;
        if ( rule == null ) {
            if ( other.rule != null ) return false;
        } else if ( !rule.equals( other.rule ) ) return false;
        return true;
    }
    
	
}

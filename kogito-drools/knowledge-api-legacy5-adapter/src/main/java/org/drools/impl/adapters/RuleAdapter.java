package org.drools.impl.adapters;

import org.kie.api.definition.rule.Rule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.drools.impl.adapters.AdapterUtil.adaptKnowledgeType;

public class RuleAdapter implements org.drools.definition.rule.Rule {

    private final Rule delegate;

    public RuleAdapter(Rule delegate) {
        this.delegate = delegate;
    }

    public String getPackageName() {
        return delegate.getPackageName();
    }

    public String getName() {
        return delegate.getName();
    }

    public Map<String, Object> getMetaData() {
        return delegate.getMetaData();
    }

    public Collection<String> listMetaAttributes() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    public Map<String, Object> getMetaAttributes() {
        throw new UnsupportedOperationException("This operation is no longer supported");
   }

    public String getMetaAttribute(String key) {
        return delegate.getPackageName();
    }

    public KnowledgeType getKnowledgeType() {
        return adaptKnowledgeType(delegate.getKnowledgeType());
    }

    public String getNamespace() {
        return delegate.getNamespace();
    }

    public String getId() {
        return delegate.getId();
    }

    public static List<org.drools.definition.rule.Rule> adaptRules(Collection<Rule> rules) {
        List<org.drools.definition.rule.Rule> result = new ArrayList<org.drools.definition.rule.Rule>();
        for (org.kie.api.definition.rule.Rule rule : rules) {
            result.add(new RuleAdapter(rule));
        }
        return result;
    }
}

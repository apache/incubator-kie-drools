package org.drools.impl.adapters;

import org.drools.definition.KnowledgeDefinition;
import org.kie.api.definition.rule.Query;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.drools.impl.adapters.AdapterUtil.adaptKnowledgeType;

public class QueryAdapter implements org.drools.definition.rule.Query {

    private final Query delegate;

    public QueryAdapter(Query delegate) {
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

    @Override
    public Map<String, Object> getMetaAttributes() {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public String getMetaAttribute(String key) {
        throw new UnsupportedOperationException("This operation is no longer supported");
    }

    @Override
    public KnowledgeType getKnowledgeType() {
        return adaptKnowledgeType(delegate.getKnowledgeType());
    }

    public String getNamespace() {
        return delegate.getNamespace();
    }

    public String getId() {
        return delegate.getId();
    }

    public static List<org.drools.definition.rule.Query> adaptQueries(Collection<org.kie.api.definition.rule.Query> queries) {
        List<org.drools.definition.rule.Query> result = new ArrayList<org.drools.definition.rule.Query>();
        for (org.kie.api.definition.rule.Query query : queries) {
            result.add(new QueryAdapter(query));
        }
        return result;
    }
}

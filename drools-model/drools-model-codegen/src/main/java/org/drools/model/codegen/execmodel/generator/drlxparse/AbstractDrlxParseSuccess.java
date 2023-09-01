package org.drools.model.codegen.execmodel.generator.drlxparse;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractDrlxParseSuccess implements DrlxParseSuccess {

    protected Set<String> watchedProperties = Collections.emptySet();

    @Override
    public AbstractDrlxParseSuccess addAllWatchedProperties( Collection<String> watchedProperties) {
        if (watchedProperties.isEmpty()) {
            return this;
        }
        if (this.watchedProperties.isEmpty()) {
            this.watchedProperties = new HashSet<>();
        }
        this.watchedProperties.addAll(watchedProperties);
        return this;
    }

    public Set<String> getWatchedProperties() {
        return watchedProperties;
    }

    @Override
    public void accept( ParseResultVoidVisitor parseVisitor ) {
        parseVisitor.onSuccess(this);
    }

    @Override
    public <T> T acceptWithReturnValue( ParseResultVisitor<T> visitor ) {
        return visitor.onSuccess(this);
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}

package org.kie.dmn.feel.lang.types;

import java.util.Map;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.util.TokenTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrappingScopeImpl implements Scope {
    public static final Logger LOG = LoggerFactory.getLogger(WrappingScopeImpl.class);

    private Scope wrapped;
    private Scope parentScope;
    private TokenTree tokenTree;

    public WrappingScopeImpl(Scope wrapping, Scope parentScope) {
        this.wrapped = wrapping;
        this.parentScope = parentScope;
    }

    @Override
    public String getName() {
        return wrapped.getName();
    }

    @Override
    public Scope getParentScope() {
        return parentScope;
    }

    @Override
    public void addChildScope(Scope scope) {
        // do nothing.
    }

    @Override
    public Map<String, Scope> getChildScopes() {
        return wrapped.getChildScopes();
    }

    @Override
    public boolean define(Symbol symbol) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Symbol resolve(String id) {
        return wrapped.resolve(id);
    }

    @Override
    public Symbol resolve(String[] qualifiedName) {
        return wrapped.resolve(qualifiedName);
    }

    @Override
    public void start(String token) {
        LOG.trace("[{}]: start() {}", "<wrapped>" + wrapped.getName(), token);
        if (tokenTree == null) {
            tokenTree = ScopeImpl.tokenTreeFromSymbols(getSymbols());
        }
        this.tokenTree.start(token);
        if (this.getParentScope() != null) {
            this.getParentScope().start(token);
        }
    }

    public boolean followUp(String token, boolean isPredict) {
        LOG.trace("[{}]: followUp() {}", "<wrapped>" + wrapped.getName(), token);
        // must call followup on parent scope
        boolean parent = this.getParentScope() != null && this.getParentScope().followUp(token, isPredict);
        return this.tokenTree.followUp(token, !isPredict) || parent;
    }

    @Override
    public Map<String, Symbol> getSymbols() {
        return wrapped.getSymbols();
    }

    @Override
    public Type getType() {
        return wrapped.getType();
    }
}

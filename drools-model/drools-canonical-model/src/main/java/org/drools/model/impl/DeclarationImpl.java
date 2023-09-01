package org.drools.model.impl;

import org.drools.model.Declaration;
import org.drools.model.DeclarationSource;
import org.drools.model.DomainClassMetadata;
import org.drools.model.Window;

public class DeclarationImpl<T> extends VariableImpl<T> implements Declaration<T> {
    private DeclarationSource source;
    private Window window;
    private DomainClassMetadata metadata;

    public DeclarationImpl(Class<T> type) {
        super(type);
    }

    public DeclarationImpl(Class<T> type, String name) {
        super(type, name);
    }

    @Override
    public DeclarationSource getSource() {
        return source;
    }

    public DeclarationImpl<T> setSource( DeclarationSource source ) {
        this.source = source;
        return this;
    }

    @Override
    public Window getWindow() {
        return window;
    }

    public DeclarationImpl<T> setWindow( Window window ) {
        this.window = window;
        return this;
    }

    @Override
    public DomainClassMetadata getMetadata() {
        return metadata;
    }

    public DeclarationImpl<T> setMetadata( DomainClassMetadata metadata ) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public boolean isEqualTo( ModelComponent var ) {
        if ( !super.isEqualTo( var ) ) return false;
        if ( !(var instanceof DeclarationImpl) ) return false;
        DeclarationImpl decl = (DeclarationImpl) var;
        if ( source != null ? !source.equals( decl.source ) : decl.source != null ) return false;
        return ModelComponent.areEqualInModel(window, decl.window );
    }
}

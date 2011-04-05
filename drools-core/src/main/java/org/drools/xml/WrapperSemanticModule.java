package org.drools.xml;

public class WrapperSemanticModule
    implements
    SemanticModule {
    private String uri;
    private SemanticModule module;

    public WrapperSemanticModule(String uri,
                                 SemanticModule module) {
        this.uri = uri;
        this.module = module;
    }
    
    public String getUri() {
        return this.uri;
    }
    
    public void addHandler(String name,
                           Handler handler) {
        module.addHandler( name,
                           handler );
    }
    
    public Handler getHandler(String name) {
        return module.getHandler( name );
    }
    
    public Handler getHandlerByClass(Class< ? > clazz) {
        return module.getHandlerByClass( clazz );
    }
    
    public SemanticModule getModule() {
        return module;
    }
    
    public void setModule(SemanticModule module) {
        this.module = module;
    }

    
    
}

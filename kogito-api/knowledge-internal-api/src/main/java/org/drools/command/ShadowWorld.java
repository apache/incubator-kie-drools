package org.drools.command;

public interface ShadowWorld extends World {
    public ShadowContext getContext(String identifier); 
}

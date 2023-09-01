package org.kie.internal.command;

import org.kie.api.runtime.Context;

public interface ContextManager{
    Context getContext( String identifier );
    Context createContext(String identifier);
}

package org.drools.clips;

import java.io.PrintStream;

public interface PrintRouterContext {

    public abstract void addRouter(String name, PrintStream out);

    public abstract boolean removeRouter(String name);

//    public abstract Map<String, PrintStream> getRouters();

}
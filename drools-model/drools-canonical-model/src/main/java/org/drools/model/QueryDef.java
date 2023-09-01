package org.drools.model;

import java.util.Arrays;

import org.drools.model.view.ViewItemBuilder;

public interface QueryDef {

    Class[] QUERIES_BY_ARITY = new Class[] {
            Query0Def.class,
            Query1Def.class,
            Query2Def.class,
            Query3Def.class,
            Query4Def.class,
            Query5Def.class,
            Query6Def.class,
            Query7Def.class,
            Query8Def.class,
            Query9Def.class,
            Query10Def.class,
    };

    static Class getQueryClassByArity(int arity) {
        return QUERIES_BY_ARITY[arity];
    }

    String getPackage();
    String getName();

    Variable<?>[] getArguments();

    default <T> Variable<T> getArg(String argName, Class<T> argType) {
        return Arrays.stream(getArguments())
                .filter(a -> a.getName().equals(argName))
                .map(a -> (Variable<T>)a)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Unknown argument: " + argName));
    }

    Query build( ViewItemBuilder... viewItemBuilders );
}

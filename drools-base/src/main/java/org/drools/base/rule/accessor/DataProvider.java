package org.drools.base.rule.accessor;

import java.io.Serializable;
import java.util.Iterator;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;

public interface DataProvider
    extends
    Serializable,
    Cloneable {

    Declaration[] getRequiredDeclarations();

    Object createContext();

    Iterator getResults(BaseTuple tuple,
                        ValueResolver valueResolver,
                        Object providerContext);

    DataProvider clone();

    void replaceDeclaration(Declaration declaration,
                            Declaration resolved);

    boolean isReactive();
}

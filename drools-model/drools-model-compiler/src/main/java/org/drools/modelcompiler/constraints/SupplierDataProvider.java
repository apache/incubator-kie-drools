package org.drools.modelcompiler.constraints;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.accessor.DataProvider;
import org.drools.model.functions.Function0;

public class SupplierDataProvider implements DataProvider {

    private final Function0 supplierFunction;
    private final boolean reactive;


    public SupplierDataProvider(Function0 providerFunction) {
        this.supplierFunction = providerFunction;
        this.reactive = false;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[] {  };
    }

    @Override
    public Object createContext() {
        return null;
    }

    @Override
    public Iterator getResults(BaseTuple tuple, ValueResolver valueResolver, Object providerContext) {
        Object result = null;
        if (supplierFunction != null) {
            result = supplierFunction.apply();
        }

        if ( result instanceof Object[] ) {
            return Arrays.asList( (Object[]) result ).iterator();
        }
        if ( result instanceof Iterator ) {
            return (( Iterator ) result);
        }
        if ( result instanceof Iterable ) {
            return (( Iterable ) result).iterator();
        }
        return Collections.singletonList( result ).iterator();
    }

    @Override
    public DataProvider clone() {
        return new SupplierDataProvider(supplierFunction );
    }

    @Override
    public void replaceDeclaration( Declaration declaration, Declaration resolved ) {

    }

    @Override
    public boolean isReactive() {
        return reactive;
    }
}

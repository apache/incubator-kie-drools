package org.drools.mvel.integrationtests.phreak.sequencing;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.MutableTypeConstraint;
import org.kie.api.runtime.rule.FactHandle;

public class AlphaConstraint extends MutableTypeConstraint {
    private Predicate1 predicate1;

    public AlphaConstraint(Predicate1 predicate1) {
        this.predicate1 = predicate1;
    }

    @Override
    public Declaration[] getRequiredDeclarations() {
        return new Declaration[0];
    }

    @Override
    public void replaceDeclaration(Declaration oldDecl, Declaration newDecl) {

    }

    @Override
    public MutableTypeConstraint clone() {
        return null;
    }

    @Override
    public boolean isTemporal() {
        return false;
    }

    @Override
    public boolean isAllowed(FactHandle handle, ValueResolver valueResolver) {
        return predicate1.test(handle.getObject());
    }

    @Override
    public boolean isAllowedCachedLeft(Object context, FactHandle handle) {
        return false;
    }

    @Override
    public boolean isAllowedCachedRight(BaseTuple tuple, Object context) {
        return false;
    }

    @Override
    public Object createContext() {
        return null;
    }
}

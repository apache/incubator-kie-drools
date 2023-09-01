package org.drools.mvel.integrationtests.phreak;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.constraint.BetaNodeFieldConstraint;
import org.drools.mvel.MVELKnowledgePackageImpl;

public class ReteTesterHelper {

    private MVELKnowledgePackageImpl pkg;

    public ReteTesterHelper() {
        this.pkg = new MVELKnowledgePackageImpl("org.drools.examples.manners");
        this.pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(this.getClass().getClassLoader()));
    }

    public InternalKnowledgePackage getPkg() {
        return pkg;
    }

    public BetaNodeFieldConstraint getBoundVariableConstraint(final Class clazz,
                                                              final String fieldName,
                                                              final Declaration declaration,
                                                              final String evaluatorString) {
        return new FakeBetaNodeFieldConstraint(clazz, fieldName, declaration, evaluatorString);
    }

    public Object getStore() {
        return pkg.getClassFieldAccessorStore();
    }
}

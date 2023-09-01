package org.drools.base.rule.accessor;

import java.io.Serializable;
import java.util.Map;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;

public interface Enabled extends Serializable {
    boolean getValue(final BaseTuple tuple,
                     final Declaration[] declarations,
                     final RuleImpl rule,
                     final ValueResolver valueResolver);

    default Declaration[] findDeclarations( Map<String, Declaration> decls) {
        return null;
    }
}

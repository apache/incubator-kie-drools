package org.drools.core.time;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;

public interface TimerExpression {

    Declaration[] getDeclarations();

    Object getValue(BaseTuple leftTuple, Declaration[] declrs, ValueResolver valueResolver);
}

package org.drools.core.spi;

import org.drools.core.rule.Declaration;

public interface MvelAccumulator extends Accumulator {

    public Declaration[] getRequiredDeclarations();

}

package org.drools.leaps;

import java.util.Arrays;

import org.drools.common.DefaultBetaConstraints;
import org.drools.rule.Column;
import org.drools.spi.DataProvider;

public class FromConstraint extends ColumnConstraints {
    private final DataProvider provider;

    public FromConstraint(final Column column,
                          final DataProvider provider,
                          final ColumnConstraints constraints) {
        super( column,
               (constraints.getAlphaContraints() == null || constraints.getAlphaContraints().length == 0) ? Arrays.asList( new FromConstraint[0] ) : Arrays.asList( constraints.getAlphaContraints() ),
               new DefaultBetaConstraints( constraints.getBetaContraints() ) );
        this.provider = provider;
    }

    public DataProvider getProvider() {
        return this.provider;
    }
}
